package com.kosmo.backend.sms;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SmsService {

    private final LecturePartRepository lecturePartRepository;
    private final SmsLogRepository smsLogRepository;

    @Value("${sms.solapi.api-key:}")
    private String apiKey;

    @Value("${sms.solapi.api-secret:}")
    private String apiSecret;

    @Value("${sms.solapi.sender:01000000000}")
    private String senderPhone;

    private DefaultMessageService messageService;

    public SmsService(LecturePartRepository lecturePartRepository, SmsLogRepository smsLogRepository) {
        this.lecturePartRepository = lecturePartRepository;
        this.smsLogRepository = smsLogRepository;
    }

    @PostConstruct
    public void init() {
        if (apiKey.isEmpty() || apiSecret.isEmpty()) {
            log.warn("SMS API 키가 설정되지 않았습니다. SMS 기능이 비활성화됩니다.");
            return;
        }
        try {
            this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
        } catch (Exception e) {
            log.warn("SMS 서비스 초기화 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void sendAttendanceWarning(Long lecturePartId) {
        LecturePartEntity part = lecturePartRepository.findById(lecturePartId)
                .orElseThrow(() -> new RuntimeException("수강생 정보를 찾을 수 없습니다."));

        String studentName = part.getUser().getUsersName();
        String lectureTitle = part.getLecture().getLectureTitle();
        String toPhone = part.getUser().getUserPhone();

        String text = studentName + " 수강생님, " + lectureTitle + " 출석률이 저조하여 해당 과정 참여에 더 성실히 해주시기 바랍니다.";

        if (hasRecentlySent(toPhone, text, Duration.ofHours(1))) {
            throw new CustomAuthException(ErrorCode.SMS_ALREADY_SENT);
        }

        sendAndLog(toPhone, text);
    }

    @Transactional
    public int sendNoticeToAllStudents(Long lectureId, String noticeText) {
        List<LecturePartEntity> parts = lecturePartRepository.findAllByLecture_LectureId(lectureId);
        int sentCount = 0;

        for (LecturePartEntity part : parts) {
            try {
                String toPhone = part.getUser().getUserPhone();
                sendAndLog(toPhone, noticeText);
                sentCount++;
            } catch (CustomAuthException e) {
                if (e.getErrorCode() == ErrorCode.SMS_ALREADY_SENT) {
                    continue;
                }
                log.warn("문자 전송 실패: {}", e.getMessage());
            } catch (Exception e) {
                log.error("알 수 없는 에러: ", e);
            }
        }

        return sentCount;
    }

    public boolean hasRecentlySent(String toPhone, String text, Duration duration) {
        LocalDateTime threshold = LocalDateTime.now().minus(duration);
        return smsLogRepository.existsByToPhoneAndTextAndSentAtAfter(toPhone, text, threshold);
    }

    private void sendAndLog(String toPhone, String text) {
        boolean success = false;
        String responseCode = "";
        String responseMessage = "";

        if (messageService == null) {
            responseCode = "DISABLED";
            responseMessage = "SMS 서비스가 비활성화 상태입니다.";
            log.info("SMS 미발송 (비활성화): to={}", toPhone);
        } else {
            Message message = new Message();
            message.setFrom(senderPhone);
            message.setTo(toPhone);
            message.setText(text);

            try {
                SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
                success = response.getStatusCode().equals("2000");
                responseCode = response.getStatusCode();
                responseMessage = response.getStatusMessage();
            } catch (Exception e) {
                responseMessage = e.getMessage();
                responseCode = "ERROR";
            }
        }

        SmsLogEntity log = SmsLogEntity.builder()
                .toPhone(toPhone)
                .fromPhone(senderPhone)
                .text(text)
                .success(success)
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .sentAt(LocalDateTime.now())
                .build();

        smsLogRepository.save(log);
    }
}

package com.kosmo.backend.sms;

import com.kosmo.backend.sms.dto.SmsNoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/admin/sms/warning/{lecturePartId}")
    public ResponseEntity<?> sendWarningMessage(@PathVariable Long lecturePartId) {
        try {
            smsService.sendAttendanceWarning(lecturePartId);
            return ResponseEntity.ok("경고 메시지를 성공적으로 보냈습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("메시지 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/admin/sms/notice/{lectureId}")
    public ResponseEntity<?> sendNoticeToAll(
            @PathVariable Long lectureId,
            @RequestBody SmsNoticeRequest request) {
        try {
            int sentCount = smsService.sendNoticeToAllStudents(lectureId, request.getMessage());
            return ResponseEntity.ok(sentCount + "명의 수강생에게 공지를 전송했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("공지 문자 전송 실패: " + e.getMessage());
        }
    }
}
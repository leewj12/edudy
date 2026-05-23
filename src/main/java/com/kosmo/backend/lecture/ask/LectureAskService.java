package com.kosmo.backend.lecture.ask;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.ask.dto.LectureAskCreateRequest;
import com.kosmo.backend.lecture.ask.dto.LectureAskResponse;
import com.kosmo.backend.lecture.ask.dto.LectureAskStatusUpdateRequest;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartStatus;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import com.kosmo.backend.user.entity.UserEntity;
import com.kosmo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureAskService {

    private final LectureAskRepository lectureAskRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final LecturePartRepository lecturePartRepository;

    @Transactional
    public void createAsk(LectureAskCreateRequest request) {
        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        // 이미 신청한 경우 (대기/보류 중인 경우만 체크)
        boolean isDuplicate = lectureAskRepository.existsByLectureAndAskPhoneAndAskStatusIn(
                lecture,
                request.getAskPhone(),
                List.of(AskStatus.WAITING, AskStatus.PENDING, AskStatus.APPROVED) // 승인 전 상태
        );

        if (isDuplicate) {
            throw new CustomAuthException(ErrorCode.ALREADY_ASKED);  // → 에러코드 추가 필요
        }

        LectureAskEntity ask = LectureAskEntity.builder()
                .lecture(lecture)
                .askName(request.getAskName())
                .askPhone(request.getAskPhone())
                .askCard(request.getAskCard())
                .askMemo(request.getAskMemo())
                .askStatus(request.getAskStatus() != null ? request.getAskStatus() : AskStatus.WAITING)
                .build();

        lectureAskRepository.save(ask);
    }

    public List<LectureAskResponse> getAskList() {
        return lectureAskRepository.findAll()
                .stream()
                .map(LectureAskResponse::fromEntity)
                .toList();
    }

    public LectureAskResponse getAskById(Long id) {
        LectureAskEntity ask = lectureAskRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_ASK_NOT_FOUND));
        return LectureAskResponse.fromEntity(ask);
    }

    @Transactional
    public void deleteAsk(Long id) {
        LectureAskEntity ask = lectureAskRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_ASK_NOT_FOUND));
        lectureAskRepository.delete(ask);
    }

    @Transactional
    public void updateAskStatus(Long lectureAskId, LectureAskStatusUpdateRequest request) {
        LectureAskEntity ask = lectureAskRepository.findById(lectureAskId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_ASK_NOT_FOUND));

        // 이미 승인 또는 반려된 경우 처리 불가
        if (ask.getAskStatus() != AskStatus.WAITING && ask.getAskStatus() != AskStatus.PENDING) {
            throw new CustomAuthException(ErrorCode.LECTURE_ASK_ALREADY_HANDLED);
        }

        // 승인 처리
        if (request.getAskStatus() == AskStatus.APPROVED) {
            // 회원 여부 확인
            UserEntity user = userRepository.findByUserPhone(ask.getAskPhone())
                    .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_PHONE_NOT_FOUND));

            // LecturePart 생성
            LecturePartEntity part = LecturePartEntity.builder()
                    .lecture(ask.getLecture())
                    .user(user)
                    .lecturePartStatus(LecturePartStatus.WAITING) // 초기 상태
                    .build();

            lecturePartRepository.save(part);

            // ✅ 수강 대기 인원 증가
            ask.getLecture().increaseWaiting();  // 아래에 엔티티 메서드 정의 필요
        }

        // 상태 업데이트 (승인 또는 반려)
        ask.updateStatus(request.getAskStatus());
    }
}

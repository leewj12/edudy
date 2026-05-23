package com.kosmo.backend.lecturepart.service;

import com.kosmo.backend.attendance.LectureAttendanceRepository;
import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.service.LectureService;
import com.kosmo.backend.lecturepart.dto.LecturePartResponse;
import com.kosmo.backend.lecturepart.dto.LecturePartUpdateRequest;
import com.kosmo.backend.lecturepart.entity.LecturePartCondition;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartRiskLevel;
import com.kosmo.backend.lecturepart.entity.LecturePartStatus;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LecturePartService {

    private final LecturePartRepository lecturePartRepository;
    private final LectureService lectureService;
    private final LectureAttendanceRepository lectureAttendanceRepository;

    public List<LecturePartResponse> findAllParts() {
        return lecturePartRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<LecturePartResponse> findByLectureId(Long lectureId) {
        return lecturePartRepository.findByLecture_LectureId(lectureId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public LecturePartResponse findById(Long partId) {
        LecturePartEntity entity = lecturePartRepository.findById(partId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));
        return toDto(entity);
    }

    public List<LecturePartResponse> getRiskyParticipants() {
        List<LecturePartRiskLevel> riskLevels = List.of(
                LecturePartRiskLevel.LOW,
                LecturePartRiskLevel.MEDIUM,
                LecturePartRiskLevel.HIGH
        );

        List<LecturePartEntity> riskyList = lecturePartRepository
                .findByLecturePartRiskLevelInAndLecturePartActionNoteNot(riskLevels, "EXCLUDED");

        return riskyList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<LecturePartResponse> getRiskyParticipantsByLectureId(Long lectureId) {
        List<LecturePartRiskLevel> riskLevels = List.of(
                LecturePartRiskLevel.LOW,
                LecturePartRiskLevel.MEDIUM,
                LecturePartRiskLevel.HIGH
        );

        List<LecturePartEntity> entities = lecturePartRepository
                .findByLecture_LectureIdAndLecturePartRiskLevelInAndLecturePartActionNoteNot(
                        lectureId, riskLevels, "EXCLUDED"
                );

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateActionNote(Long lecturePartId, String actionNote) {
        LecturePartEntity entity = lecturePartRepository.findById(lecturePartId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));


        // 문자열 검증 (필수는 아니지만 추천)
        List<String> allowedNotes = List.of("REQUESTED", "SENT_MESSAGE", "COMPLETED", "EXCLUDED");
        if (!allowedNotes.contains(actionNote)) {
            throw new CustomAuthException(ErrorCode.INVALID_ACTION_NOTE);
        }

        entity.updateLecturePartActionNote(actionNote); // setter 또는 update 메서드
    }

    private LecturePartResponse toDto(LecturePartEntity entity) {
        int score = entity.calculateRiskScore(
                Optional.ofNullable(entity.getLecture())
                        .map(LectureEntity::getLectureCurrentCnt)
                        .orElse(0L).intValue(),
                Optional.ofNullable(entity.getLecturePartAbsent())
                        .orElse(0L).intValue()
        );
        return LecturePartResponse.builder()
                .lecturePartId(entity.getLecturePartId())
                .lectureId(entity.getLecture().getLectureId())
                .lectureTitle(entity.getLecture().getLectureTitle())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getUsersName())
                .userPhone(entity.getUser().getUserPhone())
                .userBirth(entity.getUser().getUserBirth()) // ✅ 생년월일 추가
                .status(entity.getLecturePartStatus())
                .danger(entity.getLecturePartDanger())
                .month1(entity.getLecturePartMonth1())
                .month2(entity.getLecturePartMonth2())
                .month3(entity.getLecturePartMonth3())
                .month4(entity.getLecturePartMonth4())
                .month5(entity.getLecturePartMonth5())
                .month6(entity.getLecturePartMonth6())
                // ✅ 추가 필드
                .riskLevel(entity.getLecturePartRiskLevel())
                .lateCnt(entity.getLecturePartLateCnt())
                .leaveCnt(entity.getLecturePartLeaveCnt())
                .earlyLeaveCnt(entity.getLecturePartEarlyLeaveCnt())
                .absentCnt(entity.getLecturePartAbsent())   // ← 추가됨
                .actionNote(entity.getLecturePartActionNote())
                .allAttendanceRate(entity.getLecturePartAllAttRate())
                .currentAttendanceRate(entity.getCalculatedCurrentAttendanceRate()) // ✅ 계산된 값
                .riskScore(score) // ✅ 계산한 점수 포함
                .build();
    }

    @Transactional
    public void updateLecturePart(Long partId, LecturePartUpdateRequest request) {
        LecturePartEntity part = lecturePartRepository.findById(partId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        // 담당자 수정
        if (request.getEmp() != null) {
            part.updateLecturePartEmp(request.getEmp());
        }

        // 상태 변경 처리
        if (request.getStatus() != null && request.getStatus() != part.getLecturePartStatus()) {
            LecturePartStatus oldStatus = part.getLecturePartStatus();
            LecturePartStatus newStatus = request.getStatus();
            LectureEntity lecture = part.getLecture();

            // 상태 전환 처리
            if (oldStatus == LecturePartStatus.WAITING) {
                lecture.decreaseWaiting();
            } else if (oldStatus == LecturePartStatus.IN_PROGRESS || oldStatus == LecturePartStatus.COMPLETED) {
                lecture.decreaseEnrolled();
            }

            if (newStatus == LecturePartStatus.WAITING) {
                lecture.increaseWaiting();
            } else if (newStatus == LecturePartStatus.IN_PROGRESS || newStatus == LecturePartStatus.COMPLETED) {
                lecture.increaseEnrolled();
            }

            part.updateLecturePartStatus(newStatus);
        }
        // 변경 사항 JPA가 자동 감지 후 flush
    }

    @Transactional
    public void deleteLecturePart(Long partId) {
        LecturePartEntity part = lecturePartRepository.findById(partId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        LectureEntity lecture = part.getLecture();
        LecturePartStatus status = part.getLecturePartStatus();

        if (status == LecturePartStatus.WAITING) {
            lecture.decreaseWaiting();
        } else if (status == LecturePartStatus.IN_PROGRESS || status == LecturePartStatus.COMPLETED) {
            lecture.decreaseEnrolled();
        }

        lecturePartRepository.delete(part);
    }

    private LecturePartCondition calculateCondition(LectureEntity lecture, double attendanceRate) {
        Long warn = lecture.getLectureWarn();     // 예: 90
        Long danger = lecture.getLectureDanger(); // 예: 80

        if (attendanceRate >= warn) return LecturePartCondition.NORMAL;
        else if (attendanceRate >= danger) return LecturePartCondition.WARN;
        else return LecturePartCondition.DANGER;
    }

}
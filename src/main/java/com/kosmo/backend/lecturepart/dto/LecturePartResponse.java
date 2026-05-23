package com.kosmo.backend.lecturepart.dto;

import com.kosmo.backend.lecturepart.entity.LecturePartCondition;
import com.kosmo.backend.lecturepart.entity.LecturePartRiskLevel;
import com.kosmo.backend.lecturepart.entity.LecturePartStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LecturePartResponse {
    private Long lecturePartId;
    private Long lectureId;
    private String lectureTitle;
    private Long userId;
    private String userName;
    private String userPhone;
    private LocalDate userBirth; // ✅ 생년월일 추가
    private Boolean emp;
    private LecturePartStatus status;
    private LecturePartCondition danger;
    private LecturePartCondition month1;
    private LecturePartCondition month2;
    private LecturePartCondition month3;
    private LecturePartCondition month4;
    private LecturePartCondition month5;
    private LecturePartCondition month6;

    // ✅ 추가 필드
    private LecturePartRiskLevel riskLevel;
    private Long lateCnt;
    private Long leaveCnt;
    private Long earlyLeaveCnt;
    private Long absentCnt;         // ← 추가됨
    private String actionNote;
    private Long allAttendanceRate;
    private Long currentAttendanceRate;  // 실시간 계산된 값 ✅ 이거 추가!

    private int riskScore; // ✅ 위험 점수 필드 추가
}
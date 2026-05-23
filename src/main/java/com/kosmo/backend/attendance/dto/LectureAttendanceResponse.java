package com.kosmo.backend.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosmo.backend.attendance.AttReasonCode;
import com.kosmo.backend.attendance.AttStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Getter
@Builder
public class LectureAttendanceResponse {
    private Long lectureAttId;        // 출석 PK
    private Long lecturePartId;       // 수강 정보 ID
    private String userName;          // 수강생 이름

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attDate;        // 출석 날짜
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime attEntry;   // 입실 시간
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime attExit;    // 퇴실 시간
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime attLeaveStart; // 외출 시작
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime attLeaveEnd;   // 외출 종료

    private LocalDate lectureStart;
    private LocalDate lectureEnd;
    private LocalTime lectureStartTime;
    private LocalTime lectureEndTime;

    private AttStatus attStatus;      // 출석 상태
    private boolean attLate;          // 지각 여부
    private boolean attLeave;         // 외출 여부
    private boolean attEarlyLeave;    // 조퇴 여부

    // ✅ 추가
    private Long lectureId;
    private String lectureTitle;

    // ✅ 추가
    private AttReasonCode attReasonCode;
    private String attReasonDetail;
}
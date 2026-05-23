package com.kosmo.backend.attendance.dto;

import com.kosmo.backend.attendance.AttReasonCode;
import com.kosmo.backend.attendance.AttStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class AttendanceSheetResponse {
    private Long lecturePartId;            // 수강 정보 ID
    private Long lectureId;
    private String lectureTitle;           // 강의명
    private String usersName;              // 훈련생 이름
    private Long currentAttendanceRate;    // 현재 출석률

    private List<DailyAttendanceDto> attendanceRecords; // 일자별 출석 내역

    @Getter
    @Builder
    public static class DailyAttendanceDto {
        private LocalDate date;                // 출석 일자
        private AttStatus attStatus;              // 출석 상태 (ex. ATTEND, ABSENT 등)
        private AttReasonCode attReasonCode;          // 사유 코드 (ex. SICK 등)
        private String attReasonDetail;        // 상세 사유
        private Boolean attLate;               // 지각 여부
        private Boolean attLeave;              // 외출 여부
        private Boolean attEarlyLeave;         // 조퇴 여부
    }
}

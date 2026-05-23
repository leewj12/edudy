package com.kosmo.backend.attendance.dto;

import com.kosmo.backend.attendance.AttReasonCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecognizeAttendanceRequest {

    @NotNull(message = "출석 인정 사유 코드는 필수입니다.")
    private AttReasonCode reasonCode;   // 예: HOSPITAL, LEAVE, INTERVIEW 등

    private String reasonDetail;        // 상세 사유 (ex. "코로나 검사", "공기업 면접")
}

package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // 반드시 추가
public class InstrSurveyRequest {
    private Long lectureStaffId;
    private String instrQuestion1;
    private String instrQuestion2;
    private String instrQuestion3;
    private Long instrAnswer1;
    private Long instrAnswer2;
    private Long instrAnswer3;
}
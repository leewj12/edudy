package com.kosmo.backend.survey.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstrSurveyResponse {
    private Long instrSurveyId;
    private Long lectureStaffId;
    private String instrQuestion1;
    private String instrQuestion2;
    private String instrQuestion3;
    private Long instrAnswer1;
    private Long instrAnswer2;
    private Long instrAnswer3;
}

package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // 반드시 추가
public class InstrSurveyAnswerUpdateRequest {
    private Long instrSurveyId;
    private Long instrAnswer1;
    private Long instrAnswer2;
    private Long instrAnswer3;
}
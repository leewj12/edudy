package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter // 반드시 추가
public class LectureSurveyUpdateAnswerRequest {
    private Long lectureAnswer1;
    private Long lectureAnswer2;
    private Long lectureAnswer3;
    private List<InstrSurveyAnswerUpdateRequest> instrSurveys;
}
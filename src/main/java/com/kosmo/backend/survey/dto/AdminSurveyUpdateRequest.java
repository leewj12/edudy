package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter // 반드시 추가
public class AdminSurveyUpdateRequest {
    private String lectureSurveyTitle;
    private String lectureQuestion1;
    private String lectureQuestion2;
    private String lectureQuestion3;

    private List<InstrSurveyQuestionUpdateRequest> instrSurveys;

    @Getter
    public static class InstrSurveyQuestionUpdateRequest {
        private Long instrSurveyId;
        private String instrQuestion1;
        private String instrQuestion2;
        private String instrQuestion3;
    }
}
package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter // 반드시 추가
public class AdminSurveyCreateRequest {
    private Long lectureId;
    private String lectureSurveyTitle;
    private String lectureQuestion1;
    private String lectureQuestion2;
    private String lectureQuestion3;

    // 여러 명의 강사 설문 질문이 들어갈 수 있도록 리스트 형태
    private List<InstrSurveyCreateRequest> instrSurveys;

    @Getter
    public static class InstrSurveyCreateRequest {
        private Long lectureStaffId;
        private String instrQuestion1;
        private String instrQuestion2;
        private String instrQuestion3;
    }
}
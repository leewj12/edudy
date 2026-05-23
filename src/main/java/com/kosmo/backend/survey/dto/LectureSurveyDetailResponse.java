package com.kosmo.backend.survey.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LectureSurveyDetailResponse {
    private Long lectureSurveyId;
    private String lectureSurveyTitle;
    private String lectureQuestion1;
    private String lectureQuestion2;
    private String lectureQuestion3;
    private Long lectureAnswer1;
    private Long lectureAnswer2;
    private Long lectureAnswer3;
    private LocalDateTime lectureSurveyCreatedAt;
    private List<InstrSurveyResponse> instrSurveys;
}
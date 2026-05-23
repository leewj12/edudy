package com.kosmo.backend.survey.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureSurveyResponse {
    private Long lectureSurveyId;
    private Long lecturePartId;
    private String lecturePartName;
    private String lectureSurveyTitle;
    private String lectureQuestion1;
    private String lectureQuestion2;
    private String lectureQuestion3;
    private Long lectureAnswer1;
    private Long lectureAnswer2;
    private Long lectureAnswer3;
    private LocalDateTime lectureSurveyCreatedAt;
}
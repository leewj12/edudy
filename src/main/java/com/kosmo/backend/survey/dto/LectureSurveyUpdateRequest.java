package com.kosmo.backend.survey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter // 반드시 추가
public class LectureSurveyUpdateRequest {
    private Long lectureSurveyId;

    private String lectureSurveyTitle;
    private String lectureQuestion1;
    private String lectureQuestion2;
    private String lectureQuestion3;
    private Long lectureAnswer1;
    private Long lectureAnswer2;
    private Long lectureAnswer3;

    private List<InstrSurveyUpdateRequest> instrSurveys; // 수정용 강사 설문
}
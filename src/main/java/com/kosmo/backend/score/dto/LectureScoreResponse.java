package com.kosmo.backend.score.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LectureScoreResponse {
    private Long lectureScoreId;
    private Long lectureId;
    private String lectureTitle;
    private Long lecturePartId;
    private String lectureProject;
    private Long lectureProjectScore;
    private String lectureProjectComment;
    private LocalDate lectureProjectDay;
    private String lectureProjectCategory;
}

package com.kosmo.backend.score.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LectureScoreUpdateRequest {
    private String lectureProject;
    private Long lectureProjectScore;
    private String lectureProjectComment;
    private LocalDate lectureProjectDay;
    private String lectureProjectCategory;
}

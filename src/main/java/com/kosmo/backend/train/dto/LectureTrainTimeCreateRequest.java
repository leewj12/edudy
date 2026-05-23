package com.kosmo.backend.train.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureTrainTimeCreateRequest {
    private Long userId;
    private Long lectureTime;
    private String trainTitle;
    private String trainContent;
}

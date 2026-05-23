package com.kosmo.backend.lecture.dto;

import com.kosmo.backend.lecture.entity.LectureEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LecturePriorityResponse {

    private Long lectureId;
    private String lectureShortTitle;
    private Long lecturePriority;

    public static LecturePriorityResponse fromEntity(LectureEntity entity) {
        return LecturePriorityResponse.builder()
                .lectureId(entity.getLectureId())
                .lectureShortTitle(entity.getLectureShortTitle())
                .lecturePriority(entity.getLecturePriority())
                .build();
    }
}

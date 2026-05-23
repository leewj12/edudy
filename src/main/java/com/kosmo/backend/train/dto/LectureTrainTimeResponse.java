package com.kosmo.backend.train.dto;

import com.kosmo.backend.train.traintime.LectureTrainTimeEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureTrainTimeResponse {
    private Long timeId;
    private Long instructorId;
    private String instructorName;
    private Long lectureTime;
    private String trainTitle;
    private String trainContent;

    public static LectureTrainTimeResponse fromEntity(LectureTrainTimeEntity entity) {
        return LectureTrainTimeResponse.builder()
                .timeId(entity.getLectureTrainTimeId())
                .instructorId(entity.getInstructor() != null ? entity.getInstructor().getUserId() : null)
                .instructorName(entity.getInstructor() != null ? entity.getInstructor().getUsersName() : null) // ← UserEntity에 name 필드 있다고 가정
                .lectureTime(entity.getLectureTime())
                .trainTitle(entity.getTrainTitle())
                .trainContent(entity.getTrainContent())
                .build();
    }
}
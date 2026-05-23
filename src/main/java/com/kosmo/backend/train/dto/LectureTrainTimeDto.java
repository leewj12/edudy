package com.kosmo.backend.train.dto;

import com.kosmo.backend.train.traintime.LectureTrainTimeEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureTrainTimeDto {

    private Long lectureTrainTimeId;
    private Long lectureTrainId;
    private Long instructorId;
    private String instructorName;
    private Long lectureTime;        // 1~8
    private String trainTitle;
    private String trainContent;

    public static LectureTrainTimeDto fromEntity(LectureTrainTimeEntity entity) {
        return LectureTrainTimeDto.builder()
                .lectureTrainTimeId(entity.getLectureTrainTimeId())
                .lectureTrainId(entity.getLectureTrain().getLectureTrainId())
                .instructorId(entity.getInstructor().getUserId())
                .instructorName(entity.getInstructor().getUsersName())
                .lectureTime(entity.getLectureTime())
                .trainTitle(entity.getTrainTitle())
                .trainContent(entity.getTrainContent())
                .build();
    }
}
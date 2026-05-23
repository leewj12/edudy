package com.kosmo.backend.train.dto;

import com.kosmo.backend.train.LectureTrainEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class LectureTrainResponse {
    private Long trainId;
    private Long lectureId;
    private String lectureTitle;
    private LocalDate trainDate;
    private LocalDate lectureStart;
    private LocalDate lectureEnd;
    private String trainSpecial;
    private String trainAbsentees;
    private String trainLatecomers;
    private String trainEarlyLeavers;
    private String trainOutingStudents;
    private String trainInstrSign;
    private String trainAdminSign;

    private List<LectureTrainTimeResponse> timeList;

    public static LectureTrainResponse fromEntity(LectureTrainEntity entity) {
        return LectureTrainResponse.builder()
                .trainId(entity.getLectureTrainId())
                .lectureId(entity.getLecture().getLectureId())
                .lectureTitle(entity.getLecture().getLectureTitle())
                .trainDate(entity.getTrainDate())
                .lectureStart(entity.getLecture().getLectureStart())
                .lectureEnd(entity.getLecture().getLectureEnd())
                .trainSpecial(entity.getTrainSpecial())
                .trainAbsentees(entity.getTrainAbsentees())
                .trainLatecomers(entity.getTrainLatecomers())
                .trainEarlyLeavers(entity.getTrainEarlyLeavers())
                .trainOutingStudents(entity.getTrainOutingStudents())
                .trainInstrSign(entity.getTrainInstrSign())
                .trainAdminSign(entity.getTrainAdminSign())
                .timeList(entity.getTrainTimeList().stream()
                        .map(LectureTrainTimeResponse::fromEntity)
                        .toList())
                .build();
    }
}

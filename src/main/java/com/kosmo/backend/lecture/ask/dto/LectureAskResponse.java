package com.kosmo.backend.lecture.ask.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosmo.backend.lecture.ask.AskStatus;
import com.kosmo.backend.lecture.ask.LectureAskEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureAskResponse {

    private Long lectureAskId;
    private Long lectureId;
    private String lectureTitle;

    private String askName;
    private String askPhone;
    private Boolean askCard;
    private String askMemo;
    private AskStatus askStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime askCreatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime askUpdatedAt;

    public static LectureAskResponse fromEntity(LectureAskEntity entity) {
        return LectureAskResponse.builder()
                .lectureAskId(entity.getLectureAskId())
                .lectureId(entity.getLecture().getLectureId())
                .lectureTitle(entity.getLecture().getLectureTitle())
                .askName(entity.getAskName())
                .askPhone(entity.getAskPhone())
                .askCard(entity.getAskCard())
                .askMemo(entity.getAskMemo())
                .askStatus(entity.getAskStatus())
                .askCreatedAt(entity.getAskCreatedAt())
                .askUpdatedAt(entity.getAskUpdatedAt())
                .build();
    }
}
package com.kosmo.backend.train.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class LectureTrainCreateRequest {
    private Long lectureId;
    private LocalDate trainDate;
    private String trainSpecial;

    // ✅ 추가 필드
    private String trainAbsentees;
    private String trainEarlyLeavers;
    private String trainLatecomers;
    private String trainOutingStudents;
    private String trainInstrSign;
    private String trainAdminSign;

    private List<LectureTrainTimeCreateRequest> timeRequests;
}
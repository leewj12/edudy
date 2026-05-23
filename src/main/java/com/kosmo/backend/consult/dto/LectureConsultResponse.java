package com.kosmo.backend.consult.dto;

import com.kosmo.backend.consult.ConsultKeyword;
import com.kosmo.backend.consult.ConsultType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class LectureConsultResponse {
    private Long lectureConsultId;
    private Long userId;           // 🔁 변경됨
    private String userName;       // 🔁 변경됨
    private Long lecturePartId;
    private String lecturePartName;
    private String consultTitle;
    private LocalDate consultDate;
    private String consultContent;
    private String consultSpecial;
    private ConsultType consultType;
    private ConsultKeyword consultKeyword;
    private LocalDateTime consultCreatedAt;
    private LocalDateTime consultUpdatedAt;
}
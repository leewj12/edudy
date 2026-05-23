package com.kosmo.backend.consult.dto;

import com.kosmo.backend.consult.ConsultKeyword;
import com.kosmo.backend.consult.ConsultType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LectureConsultCreateRequest {
    private Long userId; // 🔁 기존 lectureStaffId → userId
    private Long lecturePartId;
    private String consultTitle;
    private LocalDate consultDate;
    private String consultContent;
    private String consultSpecial;
    private ConsultType consultType;
    private ConsultKeyword consultKeyword;
}

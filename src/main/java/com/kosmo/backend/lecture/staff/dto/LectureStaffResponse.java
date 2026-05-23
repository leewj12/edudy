package com.kosmo.backend.lecture.staff.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LectureStaffResponse {
    private Long lectureStaffId;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDate userBirth; // ✅ 추가
    private String userPhone;   // ✅ 추가;
    private Long lectureId;
    private String lectureTitle;
}
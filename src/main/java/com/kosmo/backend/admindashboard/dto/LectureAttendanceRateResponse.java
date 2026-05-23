package com.kosmo.backend.admindashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureAttendanceRateResponse {
    private Long lectureId;
    private String lectureTitle;
    private int todayAttendanceRate; // 정수형 %
}

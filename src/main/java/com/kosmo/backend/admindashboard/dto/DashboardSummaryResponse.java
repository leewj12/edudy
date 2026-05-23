package com.kosmo.backend.admindashboard.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long activeLectureCount;         // 운영 강의 수
    private Double averageAttendanceRate;    // 운영 강의 평균 출석률 (예: 87.5)
    private Long highRiskUserCount;          // 위험 수강생 수
    private Long recruitingLectureCount;     // 모집 중 강의 수
}
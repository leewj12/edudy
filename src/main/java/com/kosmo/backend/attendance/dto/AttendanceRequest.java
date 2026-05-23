package com.kosmo.backend.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private Long lecturePartId;  // 수강생과 강의 정보가 포함됨
    private double latitude;   // 사용자의 현재 GPS 위도
    private double longitude;  // 사용자의 현재 GPS 경도
}

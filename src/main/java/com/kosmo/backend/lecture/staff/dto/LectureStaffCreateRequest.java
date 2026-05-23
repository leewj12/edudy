package com.kosmo.backend.lecture.staff.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureStaffCreateRequest {
    private Long userId;     // INSTRUCTOR 권한 가진 유저
    private Long lectureId;  // 등록할 강의
}
package com.kosmo.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LectureSettingsUpdateRequest {
    private Long lectureWarn;          // 1~100 범위
    private Long lectureDanger;        // 1~100 범위
    private Long lecturePriority;      // 1, 2, 3 중 하나
    private Boolean lectureStatus;     // true/false

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutStart; // "2025-07-01T10:00:00"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutEnd;
}

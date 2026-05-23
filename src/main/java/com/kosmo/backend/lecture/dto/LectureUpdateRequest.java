package com.kosmo.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class LectureUpdateRequest {
    private String lectureTitle;
    private String lectureShortTitle;
    private String lectureDescription;
    private Long lecturePrice;
    private Long lectureCapacity;
    private String lecturePostcode;
    private String lectureAddress;
    private String lectureAddressDetail;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureStart;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lectureEnd;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime lectureStartTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime lectureEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutEnd;

    private Long lectureWarn;
    private Long lectureDanger;
    private Long lecturePriority;
    private Boolean lectureStatus;

    private Long lectureCategoryId; // ✅ 카테고리 ID 추가

    // ✅ 추가: 수정할 과목 목록
    private List<LectureSubjectUpdateDto> subjects;
}

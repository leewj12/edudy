package com.kosmo.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class LectureCreateRequest {
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

//    private String lectureThumbnail; // null 가능  // ❌ 필요 없음
    // ✅ 추가: 강의 내용 이미지
//    private String lectureContentImage;  // ❌ 필요 없음

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutStart;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutEnd;

    private Long lectureCategoryId; // ✅ 카테고리 ID 추가
}
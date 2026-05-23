package com.kosmo.backend.lecture.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kosmo.backend.banner.BannerEntity;
import com.kosmo.backend.lecture.entity.LectureEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class LectureResponse {

    private Long lectureId;
    private String lectureTitle;
    private String lectureShortTitle;
    private String lectureDescription;
    private Long lecturePrice;
    private Long lectureCapacity;
    private Long lectureEnrolled; // ✅ 추가
    private Long lectureWaiting;  // ✅ 추가

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

    private String lectureThumbnail;
    // ✅ 추가된 필드
    private String lectureContentImage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lectureLayoutEnd;

    // ✅ 추가할 필드들 - 추후 필요없으면 삭제시켜서 줄여도 됨. lectureLayoutStart 부터는 관리자가 관리하는 쪽임
    private Long lectureWarn;
    private Long lectureDanger;
    private Long lecturePriority;
    private Boolean lectureStatus;

    private String lectureCategoryName; // ✅ 카테고리 이름 포함
    // ✅ 추가: 전체 강의 일수
    private Long lectureAllDate;

    private Long bannerId;
    private String bannerImage;
    private Long bannerPriority;

    private Long droppedCnt;

    private List<LectureSubjectResponseDto> subjects;// ✅ 추가: 과목명 리스트

    public static LectureResponse fromEntity(LectureEntity lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .lectureShortTitle(lecture.getLectureShortTitle())
                .lectureDescription(lecture.getLectureDescription())
                .lecturePrice(lecture.getLecturePrice())
                .lectureCapacity(lecture.getLectureCapacity())
                .lectureEnrolled(lecture.getLectureEnrolled()) // ✅
                .lectureWaiting(lecture.getLectureWaiting())   // ✅
                .lecturePostcode(lecture.getLecturePostcode())
                .lectureAddress(lecture.getLectureAddress())
                .lectureAddressDetail(lecture.getLectureAddressDetail())
                .lectureStart(lecture.getLectureStart())
                .lectureEnd(lecture.getLectureEnd())
                .lectureStartTime(lecture.getLectureStartTime())
                .lectureEndTime(lecture.getLectureEndTime())
                .lectureThumbnail(lecture.getLectureThumbnail())
                .lectureContentImage(lecture.getLectureContentImage()) // ✅ 추가
                .lectureLayoutStart(lecture.getLectureLayoutStart())
                .lectureLayoutEnd(lecture.getLectureLayoutEnd())
                .lectureWarn(lecture.getLectureWarn())
                .lectureDanger(lecture.getLectureDanger())
                .lecturePriority(lecture.getLecturePriority())
                .lectureStatus(lecture.getLectureStatus())
                .lectureCategoryName(lecture.getLectureCategory().getLectureCategoryName())
                .lectureAllDate(lecture.getLectureAllDate()) // ✅ 여기 추가
                .droppedCnt(lecture.getDroppedCnt()) // ✅ 제적 인원 수 포함
                .subjects(
                        lecture.getLectureSubjects().stream()
                                .map(subject -> LectureSubjectResponseDto.builder()
                                        .subjectId(subject.getSubjectId())
                                        .subjectTitle(subject.getSubjectTitle())
                                        .build())
                                .toList()
                )
                .build();
    }

    public static LectureResponse fromEntityWithBanner(LectureEntity lecture, BannerEntity banner) {
        return LectureResponse.builder()
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getLectureTitle())
                .lectureShortTitle(lecture.getLectureShortTitle())
                .lectureDescription(lecture.getLectureDescription())
                .lecturePrice(lecture.getLecturePrice())
                .lectureCapacity(lecture.getLectureCapacity())
                .lectureEnrolled(lecture.getLectureEnrolled())
                .lectureWaiting(lecture.getLectureWaiting())
                .lecturePostcode(lecture.getLecturePostcode())
                .lectureAddress(lecture.getLectureAddress())
                .lectureAddressDetail(lecture.getLectureAddressDetail())
                .lectureStart(lecture.getLectureStart())
                .lectureEnd(lecture.getLectureEnd())
                .lectureStartTime(lecture.getLectureStartTime())
                .lectureEndTime(lecture.getLectureEndTime())
                .lectureThumbnail(lecture.getLectureThumbnail())
                .lectureContentImage(lecture.getLectureContentImage())
                .lectureLayoutStart(lecture.getLectureLayoutStart())
                .lectureLayoutEnd(lecture.getLectureLayoutEnd())
                .lectureWarn(lecture.getLectureWarn())
                .lectureDanger(lecture.getLectureDanger())
                .lecturePriority(lecture.getLecturePriority())
                .lectureStatus(lecture.getLectureStatus())
                .lectureCategoryName(lecture.getLectureCategory().getLectureCategoryName())
                .lectureAllDate(lecture.getLectureAllDate())
                .bannerId(banner != null ? banner.getBannerId() : null)
                .bannerImage(banner != null ? banner.getBannerImage() : null)
                .bannerPriority(banner != null ? banner.getBannerPriority() : null)
                .droppedCnt(lecture.getDroppedCnt()) // ✅ 제적 인원 수 포함
                .subjects(
                        lecture.getLectureSubjects().stream()
                                .map(subject -> LectureSubjectResponseDto.builder()
                                        .subjectId(subject.getSubjectId())
                                        .subjectTitle(subject.getSubjectTitle())
                                        .build())
                                .toList()
                )
                .build();
    }

}


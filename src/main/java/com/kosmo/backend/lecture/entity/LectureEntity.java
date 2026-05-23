package com.kosmo.backend.lecture.entity;


import com.kosmo.backend.attendance.LectureAttendanceEntity;
import com.kosmo.backend.banner.BannerEntity;
import com.kosmo.backend.lecture.ask.LectureAskEntity;
import com.kosmo.backend.lecture.category.LectureCategoryEntity;
import com.kosmo.backend.lecture.dto.LectureUpdateRequest;
import com.kosmo.backend.lecture.staff.LectureStaffEntity;
import com.kosmo.backend.lecture.subject.LectureSubjectEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartStatus;
import com.kosmo.backend.score.LectureScoreEntity;
import com.kosmo.backend.train.LectureTrainEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long lectureId;

    @Column(name = "lecture_title", nullable = false)
    private String lectureTitle;

    @Column(name = "lecture_short_title", nullable = false)
    private String lectureShortTitle;

    @Column(name = "lecture_description")
    private String lectureDescription;

    @Column(name = "lecture_price")
    private Long lecturePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_category_id", nullable = false)
    private LectureCategoryEntity lectureCategory;

    @Column(name = "lecture_capacity", nullable = false)
    private Long lectureCapacity;

    @Column(name = "lecture_enrolled", nullable = false)
    @Builder.Default
    private Long lectureEnrolled = 0L;

    @Column(name = "lecture_waiting", nullable = false)
    @Builder.Default
    private Long lectureWaiting = 0L;

    @Column(name = "lecture_postcode", nullable = false)
    private String lecturePostcode;

    @Column(name = "lecture_address", nullable = false)
    private String lectureAddress;

    @Column(name = "lecture_address_detail", nullable = false)
    private String lectureAddressDetail;

    @Column(name = "lecture_start", nullable = false)
    private LocalDate lectureStart;

    @Column(name = "lecture_end", nullable = false)
    private LocalDate lectureEnd;

    @Column(name = "lecture_all_date", nullable = false)
    private Long lectureAllDate;

    @Column(name = "lecture_current_cnt", nullable = false)
    @Builder.Default
    private Long lectureCurrentCnt = 0L;

    @Column(name = "lecture_start_time", nullable = false)
    private LocalTime lectureStartTime;

    @Column(name = "lecture_end_time", nullable = false)
    private LocalTime lectureEndTime;

    @Column(name = "lecture_thumbnail")
    private String lectureThumbnail;

    @Column(name = "lecture_content_image")
    private String lectureContentImage;

    @CreatedDate
    @Column(name = "lecture_created_at", nullable = false, updatable = false)
    private LocalDateTime lectureCreatedAt;

    @LastModifiedDate
    @Column(name = "lecture_updated_at")
    private LocalDateTime lectureUpdatedAt;

    @Column(name = "lecture_warn", nullable = false)
    @Builder.Default
    private Long lectureWarn = 90L;

    @Column(name = "lecture_danger", nullable = false)
    @Builder.Default
    private Long lectureDanger = 80L;

    @Column(name = "lecture_priority", nullable = false)
    @Builder.Default
    private Long lecturePriority = 10L;

    @Column(name = "lecture_status", nullable = false)
    @Builder.Default
    private Boolean lectureStatus = false;

    @Column(name = "lecture_layout_start")
    private LocalDateTime lectureLayoutStart;

    @Column(name = "lecture_layout_end")
    private LocalDateTime lectureLayoutEnd;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureAskEntity> lectureAsks = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureSubjectEntity> lectureSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureStaffEntity> lectureStaffs = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LecturePartEntity> lectureParts = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureAttendanceEntity> lectureAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureScoreEntity> lectureScores = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BannerEntity> banners = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTrainEntity> lectureTrains = new ArrayList<>();

    public void updateLectureThumbnail(String filename) {
        this.lectureThumbnail = filename;
    }

    public void updateLectureContentImage(String filename) {
        this.lectureContentImage = filename;
    }

    public void updateLectureInfo(LectureUpdateRequest request) {
        this.lectureTitle = request.getLectureTitle();
        this.lectureShortTitle = request.getLectureShortTitle(); // ← 추가된 부분
        this.lectureDescription = request.getLectureDescription();
        this.lecturePrice = request.getLecturePrice();
        this.lectureCapacity = request.getLectureCapacity();
        this.lecturePostcode = request.getLecturePostcode();
        this.lectureAddress = request.getLectureAddress();
        this.lectureAddressDetail = request.getLectureAddressDetail();
        this.lectureStart = request.getLectureStart();
        this.lectureEnd = request.getLectureEnd();
        this.lectureStartTime = request.getLectureStartTime();
        this.lectureEndTime = request.getLectureEndTime();
        this.lectureLayoutStart = request.getLectureLayoutStart();
        this.lectureLayoutEnd = request.getLectureLayoutEnd();
        this.lectureWarn = request.getLectureWarn();
        this.lectureDanger = request.getLectureDanger();
        this.lecturePriority = request.getLecturePriority();
        this.lectureStatus = request.getLectureStatus();
    }

    // LectureEntity.java 내부에 아래 메서드들을 추가
    public void changeWarn(Long warn) {
        this.lectureWarn = warn;
    }

    public void changeDanger(Long danger) {
        this.lectureDanger = danger;
    }

    public void changePriority(Long priority) {
        this.lecturePriority = priority;
    }

    public void changeStatus(Boolean status) {
        this.lectureStatus = status;
    }

    public void changeLayoutStart(LocalDateTime layoutStart) {
        this.lectureLayoutStart = layoutStart;
    }

    public void changeLayoutEnd(LocalDateTime layoutEnd) {
        this.lectureLayoutEnd = layoutEnd;
    }

    public void changeLectureCategory(LectureCategoryEntity category) {
        this.lectureCategory = category;
    }

    public void changeLectureWaiting(Long waitingCount) {
        this.lectureWaiting = waitingCount;
    }

    public void increaseWaiting() {
        this.lectureWaiting += 1;
    }

    public void decreaseWaiting() {
        this.lectureWaiting = Math.max(0, this.lectureWaiting - 1);
    }

    public void increaseEnrolled() {
        this.lectureEnrolled += 1;
    }

    public void decreaseEnrolled() {
        this.lectureEnrolled = Math.max(0, this.lectureEnrolled - 1);
    }

    public void changeLectureAllDate(long totalDays) {
        this.lectureAllDate = totalDays;
    }

    // 일수 증가 메서드
    public void increaseCurrentCnt() {
        this.lectureCurrentCnt += 1;
    }

    public Long getDroppedCnt() {
        if (this.lectureParts == null) return 0L;
        return lectureParts.stream()
                .filter(part -> part.getLecturePartStatus() == LecturePartStatus.DROPPED)
                .count();
    }
}
    // 추후 수강생 연관관계 추가 시 아래처럼 사용 가능
    // @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    // private List<LectureApplication> applications;

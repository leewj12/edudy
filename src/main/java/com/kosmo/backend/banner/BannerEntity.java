package com.kosmo.backend.banner;

import com.kosmo.backend.lecture.entity.LectureEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long bannerId;

    @Column(name = "banner_image", nullable = false)
    private String bannerImage;

    @Column(name = "banner_content")
    private String bannerContent;

    @Column(name = "banner_priority", nullable = false)
    private Long bannerPriority;

    @Column(name = "banner_status", nullable = false)
    @Builder.Default
    private Boolean bannerStatus = true;

    @Column(name = "banner_start")
    private LocalDateTime bannerStart;

    @Column(name = "banner_end")
    private LocalDateTime bannerEnd;

    @CreatedDate
    @Column(name = "banner_created_at", nullable = false, updatable = false)
    private LocalDateTime bannerCreatedAt;

    @LastModifiedDate
    @Column(name = "banner_updated_at")
    private LocalDateTime bannerUpdatedAt;

    // ✅ 강의 연관관계 추가 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    public void updateImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public void update(String content, Long priority, Boolean status,
                       LocalDateTime start, LocalDateTime end, LectureEntity lecture) {
        this.bannerContent = content;
        this.bannerPriority = priority;
        this.bannerStatus = status;
        this.bannerStart = start;
        this.bannerEnd = end;
        this.lecture = lecture;
    }

    public void updatePriorityStatusAndPeriod(Long priority, Boolean status,
                                              LocalDateTime start, LocalDateTime end) {
        this.bannerPriority = priority;
        this.bannerStatus = status;
        this.bannerStart = start;
        this.bannerEnd = end;
    }

    public void updatePriorityAndLecture(Long priority, LectureEntity lecture) {
        this.bannerPriority = priority;
        this.lecture = lecture;
    }

}
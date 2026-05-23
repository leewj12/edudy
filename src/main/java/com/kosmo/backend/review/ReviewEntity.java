package com.kosmo.backend.review;

import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "review_title", nullable = false)
    private String reviewTitle;

    @Column(name = "review_content", nullable = false)
    private String reviewContent;

    @Column(name = "review_thumbnail")
    private String reviewThumbnail;

    @Column(name = "review_score", nullable = false)
    private Long reviewScore;
}
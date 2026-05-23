package com.kosmo.backend.score;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.score.dto.LectureScoreUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "lecture_score")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_score_id")
    private Long lectureScoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "lecture_project", nullable = false)
    private String lectureProject;

    @Column(name = "lecture_project_score", nullable = false)
    private Long lectureProjectScore;

    @Column(name = "lecture_project_comment")
    private String lectureProjectComment;

    @Column(name = "lecture_project_day", nullable = false)
    private LocalDate lectureProjectDay;

    @Column(name = "lecture_project_category")
    private String lectureProjectCategory;

    public void update(LectureScoreUpdateRequest request) {
        this.lectureProject = request.getLectureProject();
        this.lectureProjectScore = request.getLectureProjectScore();
        this.lectureProjectComment = request.getLectureProjectComment();
        this.lectureProjectDay = request.getLectureProjectDay();
        this.lectureProjectCategory = request.getLectureProjectCategory();
    }
}

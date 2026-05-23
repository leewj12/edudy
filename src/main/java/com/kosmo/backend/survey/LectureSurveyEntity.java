package com.kosmo.backend.survey;

import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecture_survey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureSurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_survey_id")
    private Long lectureSurveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "lecture_survey_title", nullable = false)
    private String lectureSurveyTitle;

    @Column(name = "lecture_question1", nullable = false)
    private String lectureQuestion1;

    @Column(name = "lecture_question2", nullable = false)
    private String lectureQuestion2;

    @Column(name = "lecture_question3", nullable = false)
    private String lectureQuestion3;

    @Column(name = "lecture_answer1")
    private Long lectureAnswer1;

    @Column(name = "lecture_answer2")
    private Long lectureAnswer2;

    @Column(name = "lecture_answer3")
    private Long lectureAnswer3;

    @CreatedDate
    @Column(name = "lecture_survey_created_at", nullable = false, updatable = false)
    private LocalDateTime lectureSurveyCreatedAt;

    @LastModifiedDate
    @Column(name = "lecture_survey_updated_at")
    private LocalDateTime lectureSurveyUpdatedAt;

    @OneToMany(mappedBy = "lectureSurvey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstrSurveyEntity> instrSurveys = new ArrayList<>();

    public void updateLectureSurvey(String title, String q1, String q2, String q3,
                                    Long a1, Long a2, Long a3) {
        this.lectureSurveyTitle = title;
        this.lectureQuestion1 = q1;
        this.lectureQuestion2 = q2;
        this.lectureQuestion3 = q3;
        this.lectureAnswer1 = a1;
        this.lectureAnswer2 = a2;
        this.lectureAnswer3 = a3;
    }

    public void updateAnswers(Long a1, Long a2, Long a3) {
        this.lectureAnswer1 = a1;
        this.lectureAnswer2 = a2;
        this.lectureAnswer3 = a3;
    }
}
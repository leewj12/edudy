package com.kosmo.backend.survey;


import com.kosmo.backend.lecture.staff.LectureStaffEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "instr_survey")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class InstrSurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instr_survey_id")
    private Long instrSurveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_survey_id", nullable = false)
    private LectureSurveyEntity lectureSurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_staff_id", nullable = false)
    private LectureStaffEntity lectureStaff;

    @Column(name = "instr_question1", nullable = false)
    private String instrQuestion1;

    @Column(name = "instr_question2", nullable = false)
    private String instrQuestion2;

    @Column(name = "instr_question3", nullable = false)
    private String instrQuestion3;

    @Column(name = "instr_answer1")
    private Long instrAnswer1;

    @Column(name = "instr_answer2")
    private Long instrAnswer2;

    @Column(name = "instr_answer3")
    private Long instrAnswer3;

    @CreatedDate
    @Column(name = "instr_survey_created_at", nullable = false, updatable = false)
    private LocalDateTime instrSurveyCreatedAt;

    @LastModifiedDate
    @Column(name = "instr_survey_updated_at")
    private LocalDateTime instrSurveyUpdatedAt;

    public void updateInstrSurvey(String q1, String q2, String q3,
                                  Long a1, Long a2, Long a3) {
        this.instrQuestion1 = q1;
        this.instrQuestion2 = q2;
        this.instrQuestion3 = q3;
        this.instrAnswer1 = a1;
        this.instrAnswer2 = a2;
        this.instrAnswer3 = a3;
    }

    public void updateAnswers(Long a1, Long a2, Long a3) {
        this.instrAnswer1 = a1;
        this.instrAnswer2 = a2;
        this.instrAnswer3 = a3;
    }
}

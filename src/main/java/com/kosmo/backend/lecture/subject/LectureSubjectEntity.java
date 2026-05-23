package com.kosmo.backend.lecture.subject;

import com.kosmo.backend.lecture.entity.LectureEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_subject")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureSubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @Column(name = "subject_title", nullable = false)
    private String subjectTitle;

    public void changeSubjectTitle(String title) {
        this.subjectTitle = title;
    }
}

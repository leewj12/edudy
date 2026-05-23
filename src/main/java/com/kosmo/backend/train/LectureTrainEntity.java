package com.kosmo.backend.train;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.train.traintime.LectureTrainTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecture_train")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureTrainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_train_id")
    private Long lectureTrainId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @Column(name = "train_date", nullable = false)
    private LocalDate trainDate;

    @Column(name = "train_special")
    private String trainSpecial;

    @Column(name = "train_absentees")
    private String trainAbsentees;

    @Column(name = "train_latecomers")
    private String trainLatecomers;

    @Column(name = "train_early_leavers")
    private String trainEarlyLeavers;

    @Column(name = "train_outing_students")
    private String trainOutingStudents;

    @Column(name = "train_instr_sign")
    private String trainInstrSign;

    @Column(name = "train_admin_sign")
    private String trainAdminSign;

    @CreatedDate
    @Column(name = "train_created_at", nullable = false, updatable = false)
    private LocalDateTime trainCreatedAt;

    @LastModifiedDate
    @Column(name = "train_updated_at")
    private LocalDateTime trainUpdatedAt;

    @OneToMany(mappedBy = "lectureTrain", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LectureTrainTimeEntity> trainTimeList = new ArrayList<>();

    public void updateLectureTrain(
            LectureEntity lecture,
            LocalDate trainDate,
            String trainSpecial,
            String trainAbsentees,
            String trainLatecomers,
            String trainEarlyLeavers,
            String trainOutingStudents,
            String trainInstrSign,
            String trainAdminSign
    ) {
        this.lecture = lecture;
        this.trainDate = trainDate;
        this.trainSpecial = trainSpecial;
        this.trainAbsentees = trainAbsentees;
        this.trainLatecomers = trainLatecomers;
        this.trainEarlyLeavers = trainEarlyLeavers;
        this.trainOutingStudents = trainOutingStudents;
        this.trainInstrSign = trainInstrSign;
        this.trainAdminSign = trainAdminSign;
    }

}

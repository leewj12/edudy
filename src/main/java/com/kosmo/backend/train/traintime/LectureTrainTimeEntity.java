package com.kosmo.backend.train.traintime;


import com.kosmo.backend.train.LectureTrainEntity;
import com.kosmo.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_train_time")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureTrainTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_train_time_id")
    private Long lectureTrainTimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_train_id")
    private LectureTrainEntity lectureTrain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity instructor; // ✅ 강사 역할을 하는 사용자

    @Column(name = "lecture_time", nullable = false)
    private Long lectureTime; // 1~8 시간 구분

    @Column(name = "train_title")
    private String trainTitle;

    @Column(name = "train_content")
    private String trainContent;

}
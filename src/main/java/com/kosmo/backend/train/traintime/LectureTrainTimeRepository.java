package com.kosmo.backend.train.traintime;

import com.kosmo.backend.train.LectureTrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureTrainTimeRepository extends JpaRepository<LectureTrainTimeEntity, Long> {
    void deleteByLectureTrain(LectureTrainEntity lectureTrain);
}

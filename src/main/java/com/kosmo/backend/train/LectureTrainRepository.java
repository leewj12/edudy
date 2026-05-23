package com.kosmo.backend.train;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureTrainRepository extends JpaRepository<LectureTrainEntity, Long> {
    List<LectureTrainEntity> findByLecture_LectureIdOrderByTrainDateDesc(Long lectureId);
}

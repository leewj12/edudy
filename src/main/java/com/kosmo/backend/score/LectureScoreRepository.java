package com.kosmo.backend.score;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureScoreRepository extends JpaRepository<LectureScoreEntity, Long> {
    List<LectureScoreEntity> findByLecturePart_LecturePartId(Long lecturePartId);
}

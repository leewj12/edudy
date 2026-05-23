package com.kosmo.backend.survey;

import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureSurveyRepository extends JpaRepository<LectureSurveyEntity, Long> {
    List<LectureSurveyEntity> findAllByLecturePart(LecturePartEntity part);
}
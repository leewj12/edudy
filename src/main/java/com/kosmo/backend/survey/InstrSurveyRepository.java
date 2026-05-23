package com.kosmo.backend.survey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstrSurveyRepository extends JpaRepository<InstrSurveyEntity, Long> {
    List<InstrSurveyEntity> findAllByLectureSurvey(LectureSurveyEntity lectureSurvey);
}

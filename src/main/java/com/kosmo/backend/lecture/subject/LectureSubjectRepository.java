package com.kosmo.backend.lecture.subject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureSubjectRepository extends JpaRepository<LectureSubjectEntity, Long> {
    List<LectureSubjectEntity> findByLecture_LectureId(Long lectureId);
}
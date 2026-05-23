package com.kosmo.backend.lecturepart.repository;

import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartRiskLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecturePartRepository extends JpaRepository<LecturePartEntity, Long> {
    List<LecturePartEntity> findByLecture_LectureId(Long lectureId);
    List<LecturePartEntity> findAllByLecture_LectureId(Long lectureId);
    List<LecturePartEntity> findByLecture_LectureIdAndLecturePartRiskLevelInAndLecturePartActionNoteNot(
            Long lectureId, List<LecturePartRiskLevel> levels, String excludedNote);
    List<LecturePartEntity> findByLecture_LectureIdAndLecturePartRiskLevelIn(Long lectureId, List<LecturePartRiskLevel> levels);
    List<LecturePartEntity> findByLecturePartRiskLevelInAndLecturePartActionNoteNot(List<LecturePartRiskLevel> riskLevels, String excluded);

    @EntityGraph(attributePaths = {"lecture", "user"})
    List<LecturePartEntity> findAll();
}

package com.kosmo.backend.consult;

import com.kosmo.backend.consult.dto.KeywordCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LectureConsultRepository extends JpaRepository<LectureConsultEntity, Long> {
    @Query("SELECT new com.kosmo.backend.consult.dto.KeywordCountResponse(c.consultKeyword, COUNT(c)) " +
            "FROM LectureConsultEntity c " +
            "GROUP BY c.consultKeyword " +
            "ORDER BY COUNT(c) DESC")
    List<KeywordCountResponse> countConsultsByKeyword();
//    List<LectureConsultEntity> findByLectureStaff_Lecture_LectureId(Long lectureId);
//    List<LectureConsultEntity> findByLectureStaff_Lecture_LectureIdOrderByConsultDateDesc(Long lectureId);
    List<LectureConsultEntity> findByLecturePart_Lecture_LectureId(Long lectureId);
    List<LectureConsultEntity> findByLecturePart_Lecture_LectureIdOrderByConsultDateDesc(Long lectureId);

}
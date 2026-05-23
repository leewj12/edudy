package com.kosmo.backend.lecture.repository;

import com.kosmo.backend.lecture.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LectureRepository extends JpaRepository<LectureEntity, Long> {
    List<LectureEntity> findByLectureCategory_LectureCategoryId(Long categoryId);
    List<LectureEntity> findAllByOrderByLectureStartDesc();
    List<LectureEntity> findAllByOrderByLectureEnrolledDesc();

    // 최신순 (강의 시작일 기준)
    List<LectureEntity> findAllByLectureCategory_LectureCategoryIdOrderByLectureStartDesc(Long categoryId);

    // 인기순 (수강 인원 기준)
    List<LectureEntity> findAllByLectureCategory_LectureCategoryIdOrderByLectureEnrolledDesc(Long categoryId);

    List<LectureEntity> findByLecturePriorityBetweenOrderByLecturePriorityAsc(Long start, Long end);

    // 과정 제목에 특정 키워드가 포함된 강의 리스트 (내림차순 정렬)
    List<LectureEntity> findByLectureTitleContainingIgnoreCaseOrderByLectureStartDesc(String keyword);

    // admin dashboard lecture 정보 조회용
    List<LectureEntity> findByLectureStartLessThanEqualAndLectureEndGreaterThanEqual(LocalDate start, LocalDate end); // 운영중
    List<LectureEntity> findByLectureStartAfter(LocalDate date); // 모집중

}


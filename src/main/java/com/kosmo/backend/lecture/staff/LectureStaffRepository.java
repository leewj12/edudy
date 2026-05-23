package com.kosmo.backend.lecture.staff;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureStaffRepository extends JpaRepository<LectureStaffEntity, Long> {
    boolean existsByUserAndLecture(UserEntity user, LectureEntity lecture);
    List<LectureStaffEntity> findByLecture_LectureId(Long lectureId);
}
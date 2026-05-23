package com.kosmo.backend.lecture.ask;

import com.kosmo.backend.lecture.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureAskRepository extends JpaRepository<LectureAskEntity, Long> {
    boolean existsByLectureAndAskPhoneAndAskStatusIn(LectureEntity lecture, String askPhone, List<AskStatus> statuses);
}

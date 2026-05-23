package com.kosmo.backend.attendance;

import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureAttendanceRepository extends JpaRepository<LectureAttendanceEntity, Long> {
    List<LectureAttendanceEntity> findByLecture_LectureIdAndAttDate(Long lectureId, LocalDate attDate);
    List<LectureAttendanceEntity> findByLecturePart_LecturePartId(Long lecturePartId);
    List<LectureAttendanceEntity> findByLecturePart_LecturePartIdAndAttDateBetween(
            Long lecturePartId, LocalDate startDate, LocalDate endDate
    );
    List<LectureAttendanceEntity> findByLecturePart_LecturePartIdOrderByAttDateAsc(Long lecturePartId);
    Optional<LectureAttendanceEntity> findByLecturePart_LecturePartIdAndAttDate(Long lecturePartId, LocalDate attDate);
    int countByLecturePartAndAttStatus(LecturePartEntity part, AttStatus status);
    boolean existsByLecturePart_LecturePartIdAndAttDate(Long lecturePartId, LocalDate date);
    boolean existsByLecturePartAndAttStatusAndAttDate(LecturePartEntity part, AttStatus status, LocalDate date);
    List<LectureAttendanceEntity> findByLecture_LectureIdAndAttEntryBetween(Long lectureId, LocalDateTime start, LocalDateTime end);

}
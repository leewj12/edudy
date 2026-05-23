package com.kosmo.backend.attendance.holiday;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {

    // holiday_year 컬럼 기준으로 조회
    List<HolidayEntity> findByHolidayYear(Long holidayYear);

    // holiday_date 기준으로 존재 여부 확인
    boolean existsByHolidayDate(LocalDate holidayDate);
}
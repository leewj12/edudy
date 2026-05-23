package com.kosmo.backend.attendance.holiday.dto;

import com.kosmo.backend.attendance.holiday.HolidayEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HolidayResult {
    private LocalDate holidayDate;
    private String holidayName;

    public static HolidayResult fromEntity(HolidayEntity entity) {
        return new HolidayResult(entity.getHolidayDate(), entity.getHolidayName());
    }
}
package com.kosmo.backend.attendance.holiday;

import com.kosmo.backend.attendance.holiday.dto.HolidayResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping("/holidays")
    public ResponseEntity<List<LocalDate>> getHolidays(@RequestParam int year) {
        return ResponseEntity.ok(holidayService.getHolidaysForYear(year));
    }

    @GetMapping("/holiday/list")
    public ResponseEntity<List<HolidayResult>> getHolidayList(@RequestParam int year) {
        return ResponseEntity.ok(holidayService.getHolidaysForYearResult(year));
    }
}
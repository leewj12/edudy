package com.kosmo.backend.attendance;

import com.kosmo.backend.attendance.holiday.HolidayService;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartCondition;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceRiskCalculator {

    private final LectureAttendanceRepository lectureAttendanceRepository;
    private final HolidayService holidayService;

    private static final int LATE_TO_ABSENT_RATIO = 3;

    public void recalculateAttendanceCondition(LecturePartEntity part) {
        LectureEntity lecture = part.getLecture();

        // 전체 출석 가능 일 수 계산
        List<LocalDate> holidays = holidayService.getHolidaysForYearRange(
                lecture.getLectureStart().getYear(), lecture.getLectureEnd().getYear()
        );
        int totalDays = calculateAttendanceDays(lecture.getLectureStart(), lecture.getLectureEnd(), holidays);

        // 결석, 지각 횟수 계산
        int absentCount = lectureAttendanceRepository.countByLecturePartAndAttStatus(part, AttStatus.ABSENT);
        int lateCount = countLateDays(part, lecture.getLectureStart(), lecture.getLectureEnd());

        int virtualAbsent = absentCount + (lateCount / LATE_TO_ABSENT_RATIO);
        double attendanceRate = 100.0 * (totalDays - virtualAbsent) / totalDays;

        // 위험도 판단
        LecturePartCondition condition = calculateCondition(lecture, attendanceRate);
        part.updateLecturePartDanger(condition);
    }

    public void updateAllMonthlyConditions(LecturePartEntity part) {
        LectureEntity lecture = part.getLecture();
        LocalDate start = lecture.getLectureStart();
        LocalDate end = lecture.getLectureEnd();
        List<LocalDate> holidays = holidayService.getHolidaysForYearRange(start.getYear(), end.getYear());

        for (int monthIndex = 0; monthIndex < 6; monthIndex++) {
            LocalDate monthStart = start.plusMonths(monthIndex).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            if (monthStart.isAfter(end)) break;
            if (monthEnd.isAfter(end)) monthEnd = end;

            List<LocalDate> workingDays = getWorkingDaysBetween(monthStart, monthEnd, holidays);
            int totalDays = workingDays.size();
            if (totalDays == 0) continue;

            int absent = countByStatus(part, AttStatus.ABSENT, workingDays);
            int late = countLateDays(part, workingDays);
            int totalAbsent = absent + (late / LATE_TO_ABSENT_RATIO);

            double attendanceRate = 100.0 * (totalDays - totalAbsent) / totalDays;
            LecturePartCondition condition = calculateCondition(lecture, attendanceRate);

            setMonthCondition(part, monthIndex + 1, condition);
        }
    }

    private int calculateAttendanceDays(LocalDate start, LocalDate end, List<LocalDate> holidays) {
        int count = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWeekday(date) && !holidays.contains(date)) count++;
        }
        return count;
    }

    private boolean isWeekday(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    private List<LocalDate> getWorkingDaysBetween(LocalDate start, LocalDate end, List<LocalDate> holidays) {
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWeekday(date) && !holidays.contains(date)) {
                result.add(date);
            }
        }
        return result;
    }

    private int countByStatus(LecturePartEntity part, AttStatus status, List<LocalDate> days) {
        return (int) days.stream()
                .filter(date -> lectureAttendanceRepository.existsByLecturePartAndAttStatusAndAttDate(part, status, date))
                .count();
    }

    private int countLateDays(LecturePartEntity part, LocalDate start, LocalDate end) {
        return lectureAttendanceRepository.findByLecturePart_LecturePartId(part.getLecturePartId()).stream()
                .filter(att -> !att.getAttDate().isBefore(start) && !att.getAttDate().isAfter(end))
                .filter(att -> att.getAttStatus() == AttStatus.ENTRY && att.isAttLate())
                .mapToInt(att -> 1).sum();
    }

    private int countLateDays(LecturePartEntity part, List<LocalDate> dates) {
        return (int) lectureAttendanceRepository.findByLecturePart_LecturePartId(part.getLecturePartId()).stream()
                .filter(att -> dates.contains(att.getAttDate()))
                .filter(att -> att.getAttStatus() == AttStatus.ENTRY && att.isAttLate())
                .count();
    }

    private LecturePartCondition calculateCondition(LectureEntity lecture, double attendanceRate) {
        long warn = lecture.getLectureWarn();
        long danger = lecture.getLectureDanger();
        if (attendanceRate >= warn) return LecturePartCondition.NORMAL;
        else if (attendanceRate >= danger) return LecturePartCondition.WARN;
        else return LecturePartCondition.DANGER;
    }

    private void setMonthCondition(LecturePartEntity part, int month, LecturePartCondition condition) {
        switch (month) {
            case 1 -> part.updateLecturePartMonth1(condition);
            case 2 -> part.updateLecturePartMonth2(condition);
            case 3 -> part.updateLecturePartMonth3(condition);
            case 4 -> part.updateLecturePartMonth4(condition);
            case 5 -> part.updateLecturePartMonth5(condition);
            case 6 -> part.updateLecturePartMonth6(condition);
        }
    }
}
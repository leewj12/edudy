package com.kosmo.backend.admindashboard;

import com.kosmo.backend.admindashboard.dto.DashboardSummaryResponse;
import com.kosmo.backend.admindashboard.dto.LectureAttendanceRateResponse;
import com.kosmo.backend.attendance.AttStatus;
import com.kosmo.backend.attendance.LectureAttendanceEntity;
import com.kosmo.backend.attendance.LectureAttendanceRepository;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartRiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final LectureRepository lectureRepository;
    private final LectureAttendanceRepository lectureAttendanceRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        LocalDate today = LocalDate.now();

        List<LectureEntity> activeLectures =
                lectureRepository.findByLectureStartLessThanEqualAndLectureEndGreaterThanEqual(today, today);

        List<LectureEntity> recruitingLectures =
                lectureRepository.findByLectureStartAfter(today);

        double averageAttendanceRate = activeLectures.stream()
                .mapToDouble(this::calculateLectureAttendanceRate)
                .average()
                .orElse(0.0);

        long highRiskUserCount = activeLectures.stream()
                .flatMap(lecture -> lecture.getLectureParts().stream())
                .filter(part -> part.getLecturePartRiskLevel() != LecturePartRiskLevel.NORMAL)
                .count();

        return new DashboardSummaryResponse(
                (long) activeLectures.size(),
                Math.round(averageAttendanceRate * 10.0) / 10.0,
                highRiskUserCount,
                (long) recruitingLectures.size()
        );
    }

    private double calculateLectureAttendanceRate(LectureEntity lecture) {
        List<LectureAttendanceEntity> attendances = lecture.getLectureAttendances();
        if (attendances.isEmpty()) return 0.0;

        long attendedCount = attendances.stream()
                .filter(att -> att.getAttStatus() != AttStatus.ABSENT)
                .count();

        return (double) attendedCount / attendances.size() * 100.0;
    }

    public List<LectureAttendanceRateResponse> getTodayAttendanceRates() {
        LocalDate today = LocalDate.now();

        List<LectureEntity> activeLectures =
                lectureRepository.findByLectureStartLessThanEqualAndLectureEndGreaterThanEqual(today, today);

        return activeLectures.stream()
                .map(lecture -> {
                    Long lectureId = lecture.getLectureId();
                    String title = lecture.getLectureTitle();

                    // 강의의 오늘 시작/종료 시각
                    LocalDateTime startDateTime = today.atTime(lecture.getLectureStartTime());
                    LocalDateTime endDateTime = today.atTime(lecture.getLectureEndTime());

                    // 입실한 사람만 필터링해서 가져옴
                    List<LectureAttendanceEntity> attendances =
                            lectureAttendanceRepository.findByLecture_LectureIdAndAttEntryBetween(lectureId, startDateTime, endDateTime);

                    // 오늘 해당 강의 출석자 전체
                    List<LectureAttendanceEntity> todayAttendances =
                            lecture.getLectureAttendances().stream()
                                    .filter(att -> att.getAttEntry() != null && att.getAttEntry().toLocalDate().isEqual(today))
                                    .toList();

                    // 전체 대상자 수 = 해당 강의 수강생 수
                    long totalParticipants = lecture.getLectureParts().size();

                    // 입실한 인원 수
                    long entryCount = todayAttendances.size();

                    int rate = totalParticipants == 0 ? 0 :
                            (int) Math.round((double) entryCount / totalParticipants * 100.0);

                    return LectureAttendanceRateResponse.builder()
                            .lectureId(lectureId)
                            .lectureTitle(title)
                            .todayAttendanceRate(rate)
                            .build();
                })
                .toList();
    }

}

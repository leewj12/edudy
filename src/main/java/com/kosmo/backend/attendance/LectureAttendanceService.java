package com.kosmo.backend.attendance;

import com.kosmo.backend.attendance.dto.AttendanceRequest;
import com.kosmo.backend.attendance.dto.AttendanceSheetResponse;
import com.kosmo.backend.attendance.dto.LectureAttendanceResponse;
import com.kosmo.backend.attendance.holiday.HolidayService;
import com.kosmo.backend.global.config.AttendancePolicyProperties;
import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureAttendanceService {

    private final LectureRepository lectureRepository;
    private final LecturePartRepository lecturePartRepository;
    private final LectureAttendanceRepository lectureAttendanceRepository;
    private final AttendanceRiskCalculator attendanceRiskCalculator;
    private final HolidayService holidayService;
    private final AttendancePolicyProperties policy;

//    @Transactional
//    public void markAttendance(AttendanceRequest request) {
//        LecturePartEntity part = lecturePartRepository.findById(request.getLecturePartId())
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));
//
//        LocalDate date = request.getEntryTime().toLocalDate();
//
//        boolean exists = lectureAttendanceRepository.existsByLecturePart_LecturePartIdAndAttDate(part.getLecturePartId(), date);
//        if (exists) {
//            throw new CustomAuthException(ErrorCode.ATTENDANCE_ALREADY_EXISTS);  // 새 에러코드 정의 필요
//        }
//
//        LectureAttendanceEntity att = LectureAttendanceEntity.builder()
//                .lecture(part.getLecture())
//                .lecturePart(part)
//                .attDate(date)
//                .attEntry(request.getEntryTime())
//                .attStatus(AttStatus.ENTRY)  // 입실 처리
//                .build();
//
//        lectureAttendanceRepository.save(att);
//    }
    @Transactional
    public void submitAttendance(AttendanceRequest request) {
        LecturePartEntity part = lecturePartRepository.findById(request.getLecturePartId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

//        LectureEntity lecture = part.getLecture();  // 강의 정보도 이걸로 접근 가능
        // 강의실 고정 위치 (예: 302호 교실 위치)
        final double fixedLat = 37.488264;
        final double fixedLon = 126.982749;

        if (!isWithinDistance(fixedLat, fixedLon, request.getLatitude(), request.getLongitude())) {
            throw new CustomAuthException(ErrorCode.INVALID_GPS_LOCATION);
        }

        markEntry(part.getLecturePartId(), LocalDateTime.now());
    }


    @Transactional
    public void markEntry(Long lecturePartId, LocalDateTime entryTime) {
        LecturePartEntity part = lecturePartRepository.findById(lecturePartId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        LectureEntity lecture = part.getLecture();
        LocalDate date = entryTime.toLocalDate();

        // 기존 출결 데이터가 있는 경우, 출석 인정 상태라면 예외 처리
        lectureAttendanceRepository.findByLecturePart_LecturePartIdAndAttDate(lecturePartId, date)
                .ifPresent(existing -> {
                    if (existing.getAttStatus() == AttStatus.RECOGNIZED) {
                        throw new CustomAuthException(ErrorCode.RECOGNIZED_CANNOT_BE_MODIFIED);
                    } else {
                        throw new CustomAuthException(ErrorCode.ATTENDANCE_ALREADY_EXISTS);
                    }
                });

        // 출석 처리
        LocalDateTime scheduledStart = entryTime.toLocalDate().atTime(lecture.getLectureStartTime());
        LocalDateTime scheduledEnd = entryTime.toLocalDate().atTime(lecture.getLectureEndTime());

//        boolean isLate = entryTime.toLocalTime().isAfter(lecture.getLectureStartTime());
        boolean isLate = entryTime.isAfter(scheduledStart);
        long totalMinutes = java.time.Duration.between(scheduledStart, scheduledEnd).toMinutes();
        long lateMinutes = java.time.Duration.between(scheduledStart, entryTime).toMinutes();

        // 기본값은 ENTRY (출석)
        AttStatus status = AttStatus.ENTRY;
        boolean isLateFlag = false;

        if (isLate) {
            if (lateMinutes > totalMinutes / 2) {
                status = AttStatus.ABSENT;  // 50% 이상 지각 시 결석 처리
            } else {
                isLateFlag = true;
                part.increaseLateCnt(); // 증가 메서드 (setter 아님)
            }
        }

        LectureAttendanceEntity att = LectureAttendanceEntity.builder()
                .lecture(lecture)
                .lecturePart(part)
                .attDate(date)
                .attEntry(entryTime)
                .attStatus(status)
                .attLate(isLateFlag)
                .build();

        lectureAttendanceRepository.save(att);

        // ✅ 출결 저장 후 위험도 업데이트
        updateRisk(part);
    }

    public List<LectureAttendanceResponse> getAttendanceListByLectureAndDate(Long lectureId, LocalDate date) {
        List<LectureAttendanceEntity> list = lectureAttendanceRepository.findByLecture_LectureIdAndAttDate(lectureId, date);

        return list.stream().map(att -> LectureAttendanceResponse.builder()
                .lectureAttId(att.getLectureAttId())
                .lecturePartId(att.getLecturePart().getLecturePartId())
                .userName(att.getLecturePart().getUser().getUsersName())
                .attDate(att.getAttDate())
                .attEntry(att.getAttEntry())
                .attExit(att.getAttExit())
                .attLeaveStart(att.getAttLeaveStart())
                .attLeaveEnd(att.getAttLeaveEnd())
                .attStatus(att.getAttStatus())
                .attLate(att.isAttLate())
                .attLeave(att.isAttLeave())
                .attEarlyLeave(att.isAttEarlyLeave())
                .lectureId(att.getLecture().getLectureId())          // ✅ 추가
                .lectureTitle(att.getLecture().getLectureTitle())    // ✅ 추가
                .lectureStartTime(att.getLecture().getLectureStartTime())
                .lectureEndTime(att.getLecture().getLectureEndTime())
                .lectureStart(att.getLecture().getLectureStart())
                .lectureEnd(att.getLecture().getLectureEnd())
                .build())
        .toList();
    }

    @Transactional
    public void markOuting(Long attendanceId, LocalDateTime leaveStartTime) {
        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));

        if (att.getAttStatus() == AttStatus.RECOGNIZED) {
            throw new CustomAuthException(ErrorCode.RECOGNIZED_CANNOT_BE_MODIFIED);
        }

        att.updateOuting(leaveStartTime);

        // ✅ 외출 카운트 증가
        LecturePartEntity part = att.getLecturePart();
        part.increaseLeaveCnt();
    }

    @Transactional
    public void markOutingEnd(Long attendanceId, LocalDateTime leaveEndTime) {
        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));

        if (att.getAttStatus() == AttStatus.RECOGNIZED) {
            throw new CustomAuthException(ErrorCode.RECOGNIZED_CANNOT_BE_MODIFIED);
        }

        att.updateOutingEnd(leaveEndTime); // 새 메서드가 필요하면 entity에 추가

        LectureEntity lecture = att.getLecture();
        LocalDateTime lectureStart = att.getAttDate().atTime(lecture.getLectureStartTime());
        LocalDateTime lectureEnd = att.getAttDate().atTime(lecture.getLectureEndTime());

        long totalLectureMinutes = Duration.between(lectureStart, lectureEnd).toMinutes();

        // 지각 시간 계산 (attEntry > 수업 시작 시간일 경우)
        long lateMinutes = 0;
        if (att.isAttLate() && att.getAttEntry() != null){
//        if (att.getAttEntry() != null && att.getAttEntry().isAfter(lectureStart)) {
            lateMinutes = Duration.between(lectureStart, att.getAttEntry()).toMinutes();
        }

        // 외출 시간 계산 (attLeaveStart와 leaveEndTime 모두 존재하는 경우)
        long outingMinutes = 0;

        if(att.isAttLeave() && att.getAttLeaveStart() != null && leaveEndTime != null){
//        if (att.getAttLeaveStart() != null && leaveEndTime != null && leaveEndTime.isAfter(att.getAttLeaveStart())) {
            outingMinutes = Duration.between(att.getAttLeaveStart(), leaveEndTime).toMinutes();
        }

        long totalMissedMinutes = lateMinutes + outingMinutes;

        // 결석 기준 시간: 수업 시간의 50%
        if (totalMissedMinutes > totalLectureMinutes / 2) {
            // 이전에 지각/외출 처리되었으면 카운트 감소
            LecturePartEntity part = att.getLecturePart();

            if (att.isAttLate()) {
                part.decreaseLateCnt(); // 감소 메서드 필요
            }
            if (att.isAttLeave()) {
                part.decreaseLeaveCnt(); // 감소 메서드 필요
            }
            att.updateAttStatus(AttStatus.ABSENT);
            att.updateAttFalse();
            // ✅ 출결 저장 후 위험도 업데이트
            updateRisk(part);
        }
    }

    @Transactional
    public void markExit(Long attendanceId, LocalDateTime exitTime) {
        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));

        if (att.getAttStatus() == AttStatus.RECOGNIZED) {
            throw new CustomAuthException(ErrorCode.RECOGNIZED_CANNOT_BE_MODIFIED);
        }

        LectureEntity lecture = att.getLecture();
        LecturePartEntity part = att.getLecturePart(); // ✅ 조퇴 카운트 위해 참조

        LocalDateTime lectureStart = att.getAttDate().atTime(lecture.getLectureStartTime());
        LocalDateTime lectureEnd = att.getAttDate().atTime(lecture.getLectureEndTime());

        long totalLectureMinutes = Duration.between(lectureStart, lectureEnd).toMinutes();

        // 1. 지각 시간
        long lateMinutes = 0;
        if (att.isAttLate() && att.getAttEntry() != null) {
            lateMinutes = Duration.between(lectureStart, att.getAttEntry()).toMinutes();
        }

        // 2. 외출 시간
        long outingMinutes = 0;
        if (att.isAttLeave() && att.getAttLeaveStart() != null && att.getAttLeaveEnd() != null) {
            outingMinutes = Duration.between(att.getAttLeaveStart(), att.getAttLeaveEnd()).toMinutes();
        }

        // 3. 조퇴 시간
        boolean isEarlyLeave = exitTime.toLocalTime().isBefore(lecture.getLectureEndTime());
        long earlyLeaveMinutes = 0;
        if (isEarlyLeave) {
            earlyLeaveMinutes = Duration.between(exitTime, lectureEnd).toMinutes();

            // ✅ 조퇴 카운트 증가
            part.increaseEarlyLeaveCnt();
            // ✅ 출결 저장 후 위험도 업데이트
            updateRisk(part);
        }

        // 4. 총 누락 시간
        long totalMissedMinutes = lateMinutes + outingMinutes + earlyLeaveMinutes;

        // 5. 상태 업데이트
        if (totalMissedMinutes > totalLectureMinutes / 2) {
            // ✅ 기존 누락 처리된 지각/외출/조퇴 수치를 롤백
            if (att.isAttLate()) {
                part.decreaseLateCnt();
            }
            if (att.isAttLeave()) {
                part.decreaseLeaveCnt();
            }
            if (isEarlyLeave) {
                part.decreaseEarlyLeaveCnt();
            }
            att.updateAttStatus(AttStatus.ABSENT); // 결석 처리
            att.updateAttFalse();
            // ✅ 출결 저장 후 위험도 업데이트
            updateRisk(part);
        }

        // 6. 퇴실 정보 저장
        att.updateExit(exitTime, isEarlyLeave);
    }

    // ❌ 제거
//    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
    @Transactional
    public void markAbsentIfNoEntry() {
        LocalDate targetDate = LocalDate.now().minusDays(1); // 어제 날짜 기준

        if ((policy.isExcludeHolidays() && holidayService.isHoliday(targetDate)) ||
                (policy.isExcludeWeekends() && isWeekend(targetDate))) {
            return;
        }
        // 공휴일 또는 주말이면 스킵
//        if (holidayService.isHoliday(targetDate) || isWeekend(targetDate)) {
//            return;
//        }
        List<LecturePartEntity> parts = lecturePartRepository.findAll();

        for (LecturePartEntity part : parts) {
            LectureEntity lecture = part.getLecture();

            // ✅ targetDate가 강의 시작일~종료일 사이인지 확인
            if (targetDate.isBefore(lecture.getLectureStart()) || targetDate.isAfter(lecture.getLectureEnd())) {
                continue; // 날짜 범위 밖이면 스킵
            }

            boolean exists = lectureAttendanceRepository.existsByLecturePart_LecturePartIdAndAttDate(part.getLecturePartId(), targetDate);
            if (!exists) {
                LectureAttendanceEntity absent = LectureAttendanceEntity.builder()
                        .lecture(part.getLecture())
                        .lecturePart(part)
                        .attDate(targetDate)
                        .attStatus(AttStatus.ABSENT)
                        .build();
                lectureAttendanceRepository.save(absent);
            }
            // ✅ 결석 여부 상관없이 매일 위험도 갱신 (전날까지)
            attendanceRiskCalculator.recalculateAttendanceCondition(part);
            attendanceRiskCalculator.updateAllMonthlyConditions(part);
        }
    }

    @Transactional
    public void increaseLectureCurrentCntDaily() {
        LocalDate today = LocalDate.now();

        // 공휴일 또는 주말 제외 정책
        if ((policy.isExcludeHolidays() && holidayService.isHoliday(today)) ||
                (policy.isExcludeWeekends() && isWeekend(today))) {
            return;
        }

        // 전체 강의 목록 가져오기
        List<LectureEntity> lectures = lectureRepository.findAll();

        for (LectureEntity lecture : lectures) {
            // 오늘 날짜가 강의 기간에 포함되면 증가
            if (!today.isBefore(lecture.getLectureStart()) && !today.isAfter(lecture.getLectureEnd())) {
                lecture.increaseCurrentCnt();
            }
        }
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6; // 6 = 토요일, 7 = 일요일
    }

//    @Transactional
//    public void updateAttendanceStatus(Long attendanceId, AttStatus status) {
//        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));
//        if (status == AttStatus.LEAVE || status == AttStatus.MILITARY) {
//            att.updateAttStatus(status);
//        } else {
//            throw new CustomAuthException(ErrorCode.INVALID_STATUS_CHANGE);
//        }
//    }

    @Transactional
    public void recognizeAttendance(Long attendanceId, AttReasonCode reasonCode, String reasonDetail) {
        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // 이미 출석 인정 처리된 경우 중복 방지
        if (att.getAttStatus() == AttStatus.RECOGNIZED) {
            throw new CustomAuthException(ErrorCode.RECOGNIZED_ALREADY);
        }

        LecturePartEntity part = att.getLecturePart();

        // 누적 카운트 차감
        if (att.isAttLate()) part.decreaseLateCnt();
        if (att.isAttLeave()) part.decreaseLeaveCnt();
        if (att.isAttEarlyLeave()) part.decreaseEarlyLeaveCnt();

        // 출석 인정 처리
        att.updateRecognizedStatus(reasonCode, reasonDetail);
        att.updateAttFalse(); // 상태 초기화
    }

    public List<LectureAttendanceResponse> getAttendanceByLecturePart(Long partId) {
        return lectureAttendanceRepository.findByLecturePart_LecturePartId(partId)
                .stream()
                .map(att -> LectureAttendanceResponse.builder()
                        .lectureAttId(att.getLectureAttId())
                        .lecturePartId(att.getLecturePart().getLecturePartId())
                        .userName(att.getLecturePart().getUser().getUsersName())
                        .attDate(att.getAttDate())
                        .attEntry(att.getAttEntry())
                        .attExit(att.getAttExit())
                        .attLeaveStart(att.getAttLeaveStart())
                        .attLeaveEnd(att.getAttLeaveEnd())
                        .attStatus(att.getAttStatus())
                        .attLate(att.isAttLate())
                        .attLeave(att.isAttLeave())
                        .attEarlyLeave(att.isAttEarlyLeave())
                        .lectureId(att.getLecture().getLectureId())          // ✅ 추가
                        .lectureTitle(att.getLecture().getLectureTitle())    // ✅ 추가
                        // ✅ 추가 필드
                        .lectureStartTime(att.getLecture().getLectureStartTime())
                        .lectureEndTime(att.getLecture().getLectureEndTime())
                        .lectureStart(att.getLecture().getLectureStart())
                        .lectureEnd(att.getLecture().getLectureEnd())
                        .attReasonCode(att.getAttReasonCode())
                        .attReasonDetail(att.getAttReasonDetail())
                        .build())
                .toList();
    }

    public LectureAttendanceResponse getAttendanceDetail(Long attendanceId) {
        LectureAttendanceEntity att = lectureAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ATTENDANCE_NOT_FOUND));

        return LectureAttendanceResponse.builder()
                .lecturePartId(att.getLecturePart().getLecturePartId())
                .userName(att.getLecturePart().getUser().getUsersName())
                .attDate(att.getAttDate())
                .attEntry(att.getAttEntry())
                .attExit(att.getAttExit())
                .attLeaveStart(att.getAttLeaveStart())
                .attLeaveEnd(att.getAttLeaveEnd())
                .attStatus(att.getAttStatus())
                .attLate(att.isAttLate())
                .attLeave(att.isAttLeave())
                .attEarlyLeave(att.isAttEarlyLeave())
                .lectureId(att.getLecture().getLectureId())          // ✅ 추가
                .lectureTitle(att.getLecture().getLectureTitle())    // ✅ 추가
                // ✅ 추가 필드
                .lectureStartTime(att.getLecture().getLectureStartTime())
                .lectureEndTime(att.getLecture().getLectureEndTime())
                .lectureStart(att.getLecture().getLectureStart())
                .lectureEnd(att.getLecture().getLectureEnd())
                .attReasonCode(att.getAttReasonCode())
                .attReasonDetail(att.getAttReasonDetail())
                .build();
    }

    public List<LectureAttendanceResponse> getAttendanceByDateRange(Long lecturePartId, LocalDate startDate, LocalDate endDate) {
        return lectureAttendanceRepository
                .findByLecturePart_LecturePartIdAndAttDateBetween(lecturePartId, startDate, endDate)
                .stream()
                .map(att -> LectureAttendanceResponse.builder()
                        .lectureAttId(att.getLectureAttId())
                        .lecturePartId(att.getLecturePart().getLecturePartId())
                        .userName(att.getLecturePart().getUser().getUsersName())
                        .attDate(att.getAttDate())
                        .attEntry(att.getAttEntry())
                        .attExit(att.getAttExit())
                        .attLeaveStart(att.getAttLeaveStart())
                        .attLeaveEnd(att.getAttLeaveEnd())
                        .attStatus(att.getAttStatus())
                        .attLate(att.isAttLate())
                        .attLeave(att.isAttLeave())
                        .attEarlyLeave(att.isAttEarlyLeave())
                        .lectureId(att.getLecture().getLectureId())          // ✅ 추가
                        .lectureTitle(att.getLecture().getLectureTitle())    // ✅ 추가
                        // ✅ 추가 필드
                        .lectureStartTime(att.getLecture().getLectureStartTime())
                        .lectureEndTime(att.getLecture().getLectureEndTime())
                        .lectureStart(att.getLecture().getLectureStart())
                        .lectureEnd(att.getLecture().getLectureEnd())
                        .attReasonCode(att.getAttReasonCode())
                        .attReasonDetail(att.getAttReasonDetail())
                        .build())
                .toList();
    }

    public List<LectureAttendanceResponse> getAttendanceFromStartUntilYesterday(Long lecturePartId) {
        LecturePartEntity part = lecturePartRepository.findById(lecturePartId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        LocalDate startDate = part.getLecture().getLectureStart();
        LocalDate endDate = LocalDate.now().minusDays(1);

        if (startDate.isAfter(endDate)) {
            return List.of();  // 빈 리스트 반환
        }

        return getAttendanceByDateRange(lecturePartId, startDate, endDate);
    }

    public List<AttendanceSheetResponse> getFullAttendanceSheet() {
        List<LecturePartEntity> parts = lecturePartRepository.findAll();

        return parts.stream().map(part -> {
            List<LectureAttendanceEntity> attendanceList = lectureAttendanceRepository
                    .findByLecturePart_LecturePartIdOrderByAttDateAsc(part.getLecturePartId());

            List<AttendanceSheetResponse.DailyAttendanceDto> dailyRecords = attendanceList.stream()
                    .map(att -> AttendanceSheetResponse.DailyAttendanceDto.builder()
                            .date(att.getAttDate())
                            .attStatus(att.getAttStatus())
                            .attReasonCode(att.getAttReasonCode())
                            .attReasonDetail(att.getAttReasonDetail())
                            .attLate(att.isAttLate())
                            .attLeave(att.isAttLeave())
                            .attEarlyLeave(att.isAttEarlyLeave())
                            .build())
                    .collect(Collectors.toList());

            return AttendanceSheetResponse.builder()
                    .lecturePartId(part.getLecturePartId())
                    .lectureId(part.getLecture().getLectureId())
                    .lectureTitle(part.getLecture().getLectureTitle())
                    .usersName(part.getUser().getUsersName())
                    .currentAttendanceRate(part.getCalculatedCurrentAttendanceRate())
                    .attendanceRecords(dailyRecords)
                    .build();

        }).collect(Collectors.toList());
    }


    // 결석 횟수 한거에요
    private void updateRisk(LecturePartEntity part) {
        int totalDays = part.getLecture().getLectureAllDate().intValue(); // 전체 수업일 수
        // 출결 테이블에서 결석 횟수 계산
        int absentCount = lectureAttendanceRepository.countByLecturePartAndAttStatus(part, AttStatus.ABSENT);

        // DB 컬럼에도 저장
        part.updateAbsentCnt((long) absentCount);

        // 위험도 갱신
        part.updateRiskLevel(totalDays, absentCount);
    }

    public boolean isWithinDistance(double baseLat, double baseLon, double userLat, double userLon) {
        final double R = 6371e3;
        double dLat = Math.toRadians(userLat - baseLat);
        double dLon = Math.toRadians(userLon - baseLon);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(baseLat)) * Math.cos(Math.toRadians(userLat)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;
        return distance <= 200;
    }

}
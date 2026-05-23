package com.kosmo.backend.attendance;

import com.kosmo.backend.attendance.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureAttendanceController {

    private final LectureAttendanceService lectureAttendanceService;


    @PostMapping("/user/attendance/submit")
    public ResponseEntity<?> submitAttendance(
            @RequestBody AttendanceRequest request
    ) {
        lectureAttendanceService.submitAttendance(request);
        return ResponseEntity.ok("출석 완료");
    }

    // 1. 입실 (출석 등록)
    @PostMapping("/user/att/entry")
    public ResponseEntity<String> markEntry(@RequestBody AttendanceTimeRequest request) {
        lectureAttendanceService.markEntry(request.getLecturePartId(), request.getTime());
        return ResponseEntity.ok("입실이 등록되었습니다.");
    }

    // 2. 외출 시작
    @PostMapping("/user/att/outing/start")
    public ResponseEntity<String> markOuting(@RequestBody AttendanceTimeRequest request) {
        lectureAttendanceService.markOuting(request.getLectureAttId(), request.getTime());
        return ResponseEntity.ok("외출 시작이 등록되었습니다.");
    }


    // 2. 외출 종료
    @PostMapping("/user/att/outing/end")
    public ResponseEntity<String> markOutingEnd(@RequestBody AttendanceTimeRequest request) {
        lectureAttendanceService.markOutingEnd(request.getLectureAttId(), request.getTime());
        return ResponseEntity.ok("외출 종료가 등록되었습니다.");
    }


    // 3. 퇴실
    @PostMapping("/user/att/exit")
    public ResponseEntity<String> markExit(@RequestBody AttendanceTimeRequest request) {
        lectureAttendanceService.markExit(request.getLectureAttId(), request.getTime());
        return ResponseEntity.ok("퇴실이 등록되었습니다.");
    }


    // 4. 출석 상태 수동 변경 (휴가, 군휴학)
//    @PatchMapping("/admin/att/status/{lectureAttId}")
//    public ResponseEntity<String> updateStatus(@PathVariable("lectureAttId") Long attendanceId,
//                                               @RequestParam AttStatus status) {
//        lectureAttendanceService.updateAttendanceStatus(attendanceId, status);
//        return ResponseEntity.ok("출석 상태가 변경되었습니다.");
//    }
    @PatchMapping("/admin/att/recognized/{lectureAttId}")
    public ResponseEntity<String> recognizeAttendance(@PathVariable("lectureAttId") Long attendanceId,
                                                      @RequestBody @Valid RecognizeAttendanceRequest request) {
        lectureAttendanceService.recognizeAttendance(attendanceId, request.getReasonCode(), request.getReasonDetail());
        return ResponseEntity.ok("출석 인정 처리 완료");
    }

    // 관리자 전용 - 전체 이력
    // 수강생하고 같은 거지만 안쪽 내용은 달라질 수 있으니 일단 남겨둠
    // 수정하게 되면 service 메서드 구현해야함
    // 5. 관리자 - 수강생별 전체 이력 -- 왠지 그냥 안써도 될듯 user 기준으로 작성한 7번만 써도 될듯
    @GetMapping("/admin/att/user/{lecturePartId}")
    public ResponseEntity<List<LectureAttendanceResponse>> getUserAttendanceForAdmin(
            @PathVariable Long lecturePartId
    ) {
        return ResponseEntity.ok(
                lectureAttendanceService.getAttendanceByLecturePart(lecturePartId)
        );
    }

    // 6. 수강생 마이페이지 - 전체 이력
    @GetMapping("/user/att/{lecturePartId}")
    public ResponseEntity<List<LectureAttendanceResponse>> getUserAttendanceForMyPage(
            @PathVariable Long lecturePartId
    ) {
        return ResponseEntity.ok(
                lectureAttendanceService.getAttendanceByLecturePart(lecturePartId)
        );
    }

    // 7. 수강생 출결 상세 페이지
    @GetMapping("/user/att/detail/{lectureAttId}")
    public ResponseEntity<LectureAttendanceResponse> getAttendanceDetailForAdmin(
            @PathVariable("lectureAttId") Long attendanceId
    ) {
        return ResponseEntity.ok(
                lectureAttendanceService.getAttendanceDetail(attendanceId)
        );
    }

    // 8. 관리자 페이지 - 강의별 수강생들 특정 날짜 조회
    @GetMapping("/admin/att/lecture/{lectureId}")
    public ResponseEntity<List<LectureAttendanceResponse>> getDailyAttendanceForLecture(
            @PathVariable Long lectureId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
                lectureAttendanceService.getAttendanceListByLectureAndDate(lectureId, date)
        );
    }

    @GetMapping("/admin/attendance/sheet/all")
    public ResponseEntity<List<AttendanceSheetResponse>> getFullAttendanceSheet() {
        List<AttendanceSheetResponse> result = lectureAttendanceService.getFullAttendanceSheet();
        return ResponseEntity.ok(result);
    }

    // 9. 달별 조회
    @GetMapping("/user/att/monthly")
    public ResponseEntity<List<LectureAttendanceResponse>> getAttendanceByMonth(
            @RequestParam Long lecturePartId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<LectureAttendanceResponse> result =
                lectureAttendanceService.getAttendanceByDateRange(lecturePartId, start, end);

        return ResponseEntity.ok(result);
    }

    // 10. 기간 조회
    @GetMapping("/user/att/range")
    public ResponseEntity<List<LectureAttendanceResponse>> getAttendanceByDateRange(
            @RequestParam Long lecturePartId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<LectureAttendanceResponse> result =
                lectureAttendanceService.getAttendanceByDateRange(lecturePartId, startDate, endDate);

        return ResponseEntity.ok(result);
    }

    // 11. 강의 시작일 ~ 전날 자동 조회
    @GetMapping("/user/att/fromStart/{lecturePartId}")
    public ResponseEntity<List<LectureAttendanceResponse>> getAttendanceFromLectureStart(
            @PathVariable Long lecturePartId
    ) {
        return ResponseEntity.ok(
                lectureAttendanceService.getAttendanceFromStartUntilYesterday(lecturePartId)
        );
    }

}
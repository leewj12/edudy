package com.kosmo.backend.lecture.staff;

import com.kosmo.backend.lecture.staff.dto.LectureStaffCreateRequest;
import com.kosmo.backend.lecture.staff.dto.LectureStaffResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureStaffController {

    private final LectureStaffService lectureStaffService;

    @PostMapping("/admin/staff")
    public ResponseEntity<String> registerLectureStaff(@RequestBody LectureStaffCreateRequest request) {
        lectureStaffService.createLectureStaff(request);
        return ResponseEntity.ok("강사가 강의에 성공적으로 등록되었습니다.");
    }

    @GetMapping("/admin/staff/list")
    public ResponseEntity<List<LectureStaffResponse>> getLectureStaffList() {
        List<LectureStaffResponse> responses = lectureStaffService.getAllLectureStaffs();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/staff/lecture/{lectureId}")
    public ResponseEntity<List<LectureStaffResponse>> getLectureStaffsByLectureId(@PathVariable Long lectureId) {
        List<LectureStaffResponse> responses = lectureStaffService.getLectureStaffsByLectureId(lectureId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/admin/staff/{lectureStaffId}")
    public ResponseEntity<String> deleteLectureStaff(@PathVariable Long lectureStaffId) {
        lectureStaffService.deleteLectureStaff(lectureStaffId);
        return ResponseEntity.ok("강의 담당자가 삭제되었습니다.");
    }
}
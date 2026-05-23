package com.kosmo.backend.lecture.controller;

import com.kosmo.backend.lecture.dto.*;
import com.kosmo.backend.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @PostMapping("/admin/lecture")
    public ResponseEntity<?> createLecture(
            @ModelAttribute LectureCreateRequest request,
            @RequestParam(value = "LectureThumbnail", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "LectureContentImage", required = false) MultipartFile contentImageFile
    ) {
        lectureService.createLecture(request, thumbnailFile, contentImageFile);
        return ResponseEntity.ok("강의가 성공적으로 등록되었습니다.");
    }

    @PostMapping(value = "/admin/lecture/subjects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLectureWithSubjects(
            @RequestPart("request") LectureWithSubjectsCreateRequest request,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "contentImageFile", required = false) MultipartFile contentImageFile
    ) {
        lectureService.createLectureWithSubjects(request, thumbnailFile, contentImageFile);
        return ResponseEntity.ok("강의와 과목이 등록되었습니다.");
    }

    @GetMapping("/admin/lecture/list")
    public ResponseEntity<List<LectureResponse>> getAllLectures() {
        List<LectureResponse> lectures = lectureService.getAllLectures();
        return ResponseEntity.ok(lectures);
    }


    @GetMapping("/guest/lecture/list")
    public ResponseEntity<List<LectureResponse>> getAllLecturesGuest() {
        List<LectureResponse> lectures = lectureService.getAllLectures();
        return ResponseEntity.ok(lectures);
    }

    // 🔍 홈 화면 등 전체 강의 조회
    @GetMapping("/guest/lecture/list/latest")
    public ResponseEntity<List<LectureResponse>> getAllLecturesByStartDesc() {
        List<LectureResponse> response = lectureService.getAllLecturesByStartDesc();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guest/lecture/list/popular")
    public ResponseEntity<List<LectureResponse>> getLecturesByPopularity() {
        List<LectureResponse> response = lectureService.getAllLecturesByEnrolledDesc();
        return ResponseEntity.ok(response);
    }

    // 최신순
    @GetMapping("/guest/lecture/list/category/{categoryId}/latest")
    public ResponseEntity<List<LectureResponse>> getLatestLecturesByCategory(@PathVariable Long categoryId) {
        List<LectureResponse> response = lectureService.findLatestLecturesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // 인기순
    @GetMapping("/guest/lecture/list/category/{categoryId}/popular")
    public ResponseEntity<List<LectureResponse>> getPopularLecturesByCategory(@PathVariable Long categoryId) {
        List<LectureResponse> response = lectureService.findPopularLecturesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guest/lecture/search")
    public ResponseEntity<List<LectureResponse>> searchLectures(@RequestParam("keyword") String keyword) {
        List<LectureResponse> results = lectureService.searchLecturesByTitle(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/lecture/list")
    public ResponseEntity<List<LectureResponse>> getAllLecturesUser() {
        List<LectureResponse> lectures = lectureService.getAllLectures();
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/admin/lecture/{lectureId}")
    public ResponseEntity<LectureResponse> getLectureById(@PathVariable Long lectureId) {
        LectureResponse lecture = lectureService.getLectureById(lectureId);
        return ResponseEntity.ok(lecture);
    }

    @GetMapping("/guest/lecture/{lectureId}")
    public ResponseEntity<LectureResponse> getLectureByIdGuest(@PathVariable Long lectureId) {
        LectureResponse lecture = lectureService.getLectureById(lectureId);
        return ResponseEntity.ok(lecture);
    }

    @GetMapping("/admin/lecture/list/category/{categoryId}")
    public ResponseEntity<List<LectureResponse>> getLecturesByCategory(@PathVariable Long categoryId) {
        List<LectureResponse> lectures = lectureService.getLecturesByCategory(categoryId);
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/guest/lecture/list/priority")
    public ResponseEntity<List<LecturePriorityResponse>> getPriorityLectures() {
        return ResponseEntity.ok(lectureService.getTopPriorityLectures());
    }

    @DeleteMapping("/admin/lecture/{lectureId}")
    public ResponseEntity<?> deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // LectureController.java
    @PutMapping(value = "/admin/lecture/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLecture(
            @PathVariable Long id,
            @RequestPart("request") LectureUpdateRequest request,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnail,
            @RequestParam(value = "contentImageFile", required = false) MultipartFile contentImage

    ) {
        lectureService.updateLecture(id, request, thumbnail, contentImage);
        return ResponseEntity.ok("강의 정보가 수정되었습니다.");
    }

    @PatchMapping("/admin/lecture/{id}/settings")
    public ResponseEntity<?> updateLectureSettings(
            @PathVariable Long id,
            @RequestBody LectureSettingsUpdateRequest request) {
        lectureService.updateLectureSettings(id, request);
        return ResponseEntity.ok().build();
    }

}
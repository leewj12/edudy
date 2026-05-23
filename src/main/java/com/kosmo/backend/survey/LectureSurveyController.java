package com.kosmo.backend.survey;

import com.kosmo.backend.survey.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureSurveyController {

    private final LectureSurveyService lectureSurveyService;

    // 관리자 과정 전체 수강생에게 설문 등록
    @PostMapping("/admin/survey/{lectureId}")
    public ResponseEntity<String> createSurveyForLecture(
            @PathVariable Long lectureId,
            @RequestBody AdminSurveyCreateRequest request) {
        lectureSurveyService.createSurveyForAllLectureParts(lectureId, request);
        return ResponseEntity.ok("과정 전체 수강생에게 설문이 배포되었습니다.");
    }

    // 관리자 설문지 질문 수정
    @PatchMapping("/admin/survey/questions/{lectureSurveyId}")
    public ResponseEntity<String> updateSurveyQuestions(@PathVariable Long lectureSurveyId,
                                                        @RequestBody AdminSurveyUpdateRequest request) {
        lectureSurveyService.updateSurveyQuestionsOnly(lectureSurveyId, request);
        return ResponseEntity.ok("설문 질문이 성공적으로 수정되었습니다.");
    }

    // 관리자 과정별 설문 리스트 조회
    @GetMapping("/admin/survey/lecture/{lectureId}")
    public ResponseEntity<List<LectureSurveyResponse>> getSurveysByLecture(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureSurveyService.getSurveysByLecture(lectureId));
    }

    // 관리자 설문 상세 조회
    @GetMapping("/admin/survey/{lectureSurveyId}")
    public ResponseEntity<LectureSurveyDetailResponse> getSurveyDetailAdmin(@PathVariable Long lectureSurveyId) {
        return ResponseEntity.ok(lectureSurveyService.getSurveyDetail(lectureSurveyId));
    }

//    @PostMapping("/user/survey")
//    public ResponseEntity<String> createSurvey(@RequestBody LectureSurveyRequest request) {
//        lectureSurveyService.createSurveyWithInstrs(request);
//        return ResponseEntity.ok("설문이 성공적으로 저장되었습니다.");
//    }

    // 특정 수강생 전체 설문 목록 조회
    @GetMapping("/user/survey/list/{lecturePartId}")
    public ResponseEntity<List<LectureSurveyResponse>> getAllSurveys(@PathVariable Long lecturePartId) {
        return ResponseEntity.ok(lectureSurveyService.getSurveysByLecturePart(lecturePartId));
    }

    // 수강생 설문 상세 조회
    @GetMapping("/user/survey/{lectureSurveyId}")
    public ResponseEntity<LectureSurveyDetailResponse> getSurveyDetail(@PathVariable Long lectureSurveyId) {
        return ResponseEntity.ok(lectureSurveyService.getSurveyDetail(lectureSurveyId));
    }

//    @PatchMapping("/user/survey/update")
//    public ResponseEntity<String> updateSurvey(@RequestBody LectureSurveyUpdateRequest request) {
//        lectureSurveyService.updateSurveyWithInstrs(request);
//        return ResponseEntity.ok("설문이 성공적으로 수정되었습니다.");
//    }

    // 수강생 설문 답변 등록
    @PatchMapping("/user/survey/answers/{lectureSurveyId}")
    public ResponseEntity<String> updateSurveyAnswers(@PathVariable Long lectureSurveyId,
                                                      @RequestBody LectureSurveyUpdateAnswerRequest request) {
        lectureSurveyService.updateAnswersOnly(lectureSurveyId, request);
        return ResponseEntity.ok("답변이 성공적으로 저장되었습니다.");
    }


    // 관리자 설문 삭제
    @DeleteMapping("/admin/survey/{lectureSurveyId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long lectureSurveyId) {
        lectureSurveyService.deleteSurvey(lectureSurveyId);
        return ResponseEntity.ok("설문이 성공적으로 삭제되었습니다.");
    }
}
package com.kosmo.backend.score;

import com.kosmo.backend.score.dto.LectureScoreCreateRequest;
import com.kosmo.backend.score.dto.LectureScoreResponse;
import com.kosmo.backend.score.dto.LectureScoreUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureScoreController {

    private final LectureScoreService lectureScoreService;

    @PostMapping("/admin/score")
    public ResponseEntity<String> createLectureScore(@RequestBody LectureScoreCreateRequest request) {
        lectureScoreService.createLectureScore(request);
        return ResponseEntity.ok("프로젝트 성적이 등록되었습니다.");
    }

    // 🔍 성적 리스트 조회 (특정 수강생 기준)
    @GetMapping("/admin/score/list/{lecturePartId}")
    public ResponseEntity<List<LectureScoreResponse>> getScoreList(@PathVariable Long lecturePartId) {
        return ResponseEntity.ok(lectureScoreService.getScoresByLecturePartId(lecturePartId));
    }

    // 🔍 성적 리스트 조회 (수강생 본인 기준)
    @GetMapping("/user/score/list/{lecturePartId}")
    public ResponseEntity<List<LectureScoreResponse>> getScoreListUser(@PathVariable Long lecturePartId) {
        return ResponseEntity.ok(lectureScoreService.getScoresByLecturePartId(lecturePartId));
    }

    // 🔍 성적 상세 조회
    @GetMapping("/admin/score/{scoreId}")
    public ResponseEntity<LectureScoreResponse> getScoreDetail(@PathVariable Long scoreId) {
        return ResponseEntity.ok(lectureScoreService.getScoreDetail(scoreId));
    }

    @PatchMapping("/admin/score/{scoreId}")
    public ResponseEntity<String> updateLectureScore(
            @PathVariable Long scoreId,
            @RequestBody LectureScoreUpdateRequest request
    ) {
        lectureScoreService.updateLectureScore(scoreId, request);
        return ResponseEntity.ok("프로젝트 성적이 수정되었습니다.");
    }

    @DeleteMapping("/admin/score/{scoreId}")
    public ResponseEntity<String> deleteLectureScore(@PathVariable Long scoreId) {
        lectureScoreService.deleteLectureScore(scoreId);
        return ResponseEntity.ok("프로젝트 성적이 삭제되었습니다.");
    }

}
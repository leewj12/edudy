package com.kosmo.backend.consult;

import com.kosmo.backend.consult.dto.KeywordCountResponse;
import com.kosmo.backend.consult.dto.LectureConsultCreateRequest;
import com.kosmo.backend.consult.dto.LectureConsultResponse;
import com.kosmo.backend.consult.dto.LectureConsultUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureConsultController {

    private final LectureConsultService lectureConsultService;

    @PostMapping("/admin/consult")
    public ResponseEntity<String> createConsult(@RequestBody LectureConsultCreateRequest request) {
        lectureConsultService.createLectureConsult(request);
        return ResponseEntity.ok("상담일지가 등록되었습니다.");
    }

    @GetMapping("/admin/consult/list")
    public ResponseEntity<List<LectureConsultResponse>> getAllConsults() {
        List<LectureConsultResponse> responses = lectureConsultService.getAllConsults();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/consult/list/{lectureId}")
    public ResponseEntity<List<LectureConsultResponse>> getConsultsByLectureId(
            @PathVariable Long lectureId
    ) {
        List<LectureConsultResponse> responses = lectureConsultService.getConsultsByLectureId(lectureId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/consult/{consultId}")
    public ResponseEntity<LectureConsultResponse> getConsultDetail(@PathVariable Long consultId) {
        LectureConsultResponse response = lectureConsultService.getConsultDetail(consultId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/consult/{consultId}")
    public ResponseEntity<String> updateConsult(
            @PathVariable Long consultId,
            @RequestBody LectureConsultUpdateRequest request
    ) {
        lectureConsultService.updateLectureConsult(consultId, request);
        return ResponseEntity.ok("상담일지가 수정되었습니다.");
    }

    @DeleteMapping("/admin/consult/{consultId}")
    public ResponseEntity<String> deleteConsult(@PathVariable Long consultId) {
        lectureConsultService.deleteLectureConsult(consultId);
        return ResponseEntity.ok("상담일지가 삭제되었습니다.");
    }

    @GetMapping("/admin/consult/keyword/count")
    public ResponseEntity<List<KeywordCountResponse>> getKeywordCount() {
        return ResponseEntity.ok(lectureConsultService.getKeywordStatistics());
    }
}
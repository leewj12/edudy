package com.kosmo.backend.lecture.ask;

import com.kosmo.backend.lecture.ask.dto.LectureAskCreateRequest;
import com.kosmo.backend.lecture.ask.dto.LectureAskResponse;
import com.kosmo.backend.lecture.ask.dto.LectureAskStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureAskController {

    private final LectureAskService lectureAskService;

    @PostMapping("/ask")
    public ResponseEntity<?> createAsk(@RequestBody LectureAskCreateRequest request) {
        lectureAskService.createAsk(request);
        return ResponseEntity.ok("문의가 성공적으로 등록되었습니다.");
    }

    @GetMapping("/admin/ask/list")
    public ResponseEntity<List<LectureAskResponse>> getAskList() {
        List<LectureAskResponse> asks = lectureAskService.getAskList();
        return ResponseEntity.ok(asks);
    }

    @GetMapping("/admin/ask/{lectureAskId}")
    public ResponseEntity<LectureAskResponse> getAskDetail(@PathVariable Long lectureAskId) {
        LectureAskResponse ask = lectureAskService.getAskById(lectureAskId);
        return ResponseEntity.ok(ask);
    }

    @PatchMapping("/admin/ask/update/{lectureAskId}")
    public ResponseEntity<?> updateAskStatus(
            @PathVariable Long lectureAskId,
            @RequestBody LectureAskStatusUpdateRequest request
    ) {
        lectureAskService.updateAskStatus(lectureAskId, request);
        return ResponseEntity.ok("문의 상태가 변경되었습니다.");
    }

    @DeleteMapping("/admin/ask/delete/{lectureAskId}")
    public ResponseEntity<?> deleteAsk(@PathVariable Long lectureAskId) {
        lectureAskService.deleteAsk(lectureAskId);
        return ResponseEntity.noContent().build(); // 204
    }
}

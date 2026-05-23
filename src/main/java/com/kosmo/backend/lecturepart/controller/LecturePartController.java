package com.kosmo.backend.lecturepart.controller;

import com.kosmo.backend.lecturepart.dto.LecturePartActionNoteUpdateRequest;
import com.kosmo.backend.lecturepart.dto.LecturePartResponse;
import com.kosmo.backend.lecturepart.dto.LecturePartUpdateRequest;
import com.kosmo.backend.lecturepart.service.LecturePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LecturePartController {

    private final LecturePartService lecturePartService;

    // 전체 참가자 조회
    @GetMapping("/admin/lecture/part/list")
    public ResponseEntity<List<LecturePartResponse>> getAllParts() {
        return ResponseEntity.ok(lecturePartService.findAllParts());
    }

    // 특정 강의 참가자 조회
    @GetMapping("/admin/lecture/part/list/{lectureId}")
    public ResponseEntity<List<LecturePartResponse>> getByLectureId(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lecturePartService.findByLectureId(lectureId));
    }

    // 참가자 단건 조회
    @GetMapping("/admin/lecture/part/{partId}")
    public ResponseEntity<LecturePartResponse> getById(@PathVariable Long partId) {
        return ResponseEntity.ok(lecturePartService.findById(partId));
    }

    //위험 참가자 조회
    @GetMapping("/admin/lecture/part/risk/list")
    public ResponseEntity<List<LecturePartResponse>> getRiskyParticipants() {
        List<LecturePartResponse> responses = lecturePartService.getRiskyParticipants();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/lecture/part/risk/list/{lectureId}")
    public ResponseEntity<List<LecturePartResponse>> getRiskyParticipantsByLectureId(
            @PathVariable Long lectureId) {
        List<LecturePartResponse> responseList = lecturePartService.getRiskyParticipantsByLectureId(lectureId);
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/admin/lecture/part/actionNote/{lecturePartId}")
    public ResponseEntity<String> updateActionNote(
            @PathVariable Long lecturePartId,
            @RequestBody LecturePartActionNoteUpdateRequest request
    ) {
        lecturePartService.updateActionNote(lecturePartId, request.getActionNote());
        return ResponseEntity.ok("상담 상태가 업데이트되었습니다.");
    }

    @PatchMapping("/admin/lecture/part/update/{partId}")
    public ResponseEntity<Void> updatePart(@PathVariable Long partId,
                                           @RequestBody LecturePartUpdateRequest request) {
        lecturePartService.updateLecturePart(partId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/lecture/part/delete/{partId}")
    public ResponseEntity<Void> deletePart(@PathVariable Long partId) {
        lecturePartService.deleteLecturePart(partId);
        return ResponseEntity.noContent().build();
    }
}

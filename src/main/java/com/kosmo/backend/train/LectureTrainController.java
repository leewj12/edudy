package com.kosmo.backend.train;

import com.kosmo.backend.train.dto.LectureTrainCreateRequest;
import com.kosmo.backend.train.dto.LectureTrainResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureTrainController {

    private final LectureTrainService lectureTrainService;

    @PostMapping("/admin/train")
    public ResponseEntity<String> createTrain(@RequestBody LectureTrainCreateRequest request) {
        lectureTrainService.createLectureTrain(request);
        return ResponseEntity.ok("훈련일지가 등록되었습니다.");
    }

    // ✅ 1. 훈련일지 목록 조회 (과정 기준)
    @GetMapping("/admin/train/lecture/{lectureId}")
    public ResponseEntity<List<LectureTrainResponse>> getLectureTrainList(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureTrainService.getLectureTrainList(lectureId));
    }

    // ✅ 2. 훈련일지 상세 조회
    @GetMapping("/admin/train/{trainId}")
    public ResponseEntity<LectureTrainResponse> getLectureTrainDetail(@PathVariable Long trainId) {
        return ResponseEntity.ok(lectureTrainService.getLectureTrainDetail(trainId));
    }

    @PutMapping("/admin/train/{trainId}")
    public ResponseEntity<String> updateLectureTrain(
            @PathVariable Long trainId,
            @RequestBody LectureTrainCreateRequest request
    ) {
        lectureTrainService.updateLectureTrain(trainId, request);
        return ResponseEntity.ok("훈련일지가 수정되었습니다.");
    }

    @DeleteMapping("/admin/train/{trainId}")
    public ResponseEntity<?> deleteLectureTrain(@PathVariable Long trainId) {
        lectureTrainService.deleteLectureTrain(trainId);
        return ResponseEntity.ok("훈련일지가 삭제되었습니다.");
    }


}
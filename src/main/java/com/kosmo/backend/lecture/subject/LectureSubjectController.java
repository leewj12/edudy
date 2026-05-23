package com.kosmo.backend.lecture.subject;

import com.kosmo.backend.lecture.subject.dto.LectureSubjectCreateListRequest;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectCreateRequest;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectResponse;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LectureSubjectController {

    private final LectureSubjectService lectureSubjectService;

    @PostMapping("/admin/subject")
    public ResponseEntity<?> createSubject(@RequestBody LectureSubjectCreateRequest request) {
        lectureSubjectService.createSubject(request);
        return ResponseEntity.ok("과목이 등록되었습니다.");
    }

    @PostMapping("/admin/subject/bulk")
    public ResponseEntity<?> createSubjects(@RequestBody LectureSubjectCreateListRequest request) {
        lectureSubjectService.createSubjects(request);
        return ResponseEntity.ok("과목이 일괄 등록되었습니다.");
    }

    @GetMapping("/admin/subject/list/{lectureId}")
    public ResponseEntity<List<LectureSubjectResponse>> getSubjects(@PathVariable Long lectureId) {
        List<LectureSubjectResponse> subjects = lectureSubjectService.getSubjectsByLectureId(lectureId);
        return ResponseEntity.ok(subjects);
    }

    @PutMapping("/admin/subject/update/{subjectId}")
    public ResponseEntity<?> updateSubject(
            @PathVariable Long subjectId,
            @RequestBody LectureSubjectUpdateRequest request) {
        lectureSubjectService.updateSubject(subjectId, request);
        return ResponseEntity.ok("과목이 수정되었습니다.");
    }

    @DeleteMapping("/admin/subject/delete/{subjectId}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long subjectId) {
        lectureSubjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
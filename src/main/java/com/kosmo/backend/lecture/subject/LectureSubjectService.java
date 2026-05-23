package com.kosmo.backend.lecture.subject;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectCreateListRequest;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectCreateRequest;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectResponse;
import com.kosmo.backend.lecture.subject.dto.LectureSubjectUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureSubjectService {

    private final LectureSubjectRepository lectureSubjectRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void createSubject(LectureSubjectCreateRequest request) {
        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        LectureSubjectEntity subject = LectureSubjectEntity.builder()
                .lecture(lecture)
                .subjectTitle(request.getSubjectTitle())
                .build();

        lectureSubjectRepository.save(subject);
    }

    @Transactional
    public void createSubjects(LectureSubjectCreateListRequest request) {
        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        List<LectureSubjectEntity> subjects = request.getSubjectTitles().stream()
                .map(title -> LectureSubjectEntity.builder()
                        .lecture(lecture)
                        .subjectTitle(title)
                        .build())
                .toList();

        lectureSubjectRepository.saveAll(subjects);
    }

    public List<LectureSubjectResponse> getSubjectsByLectureId(Long lectureId) {
        return lectureSubjectRepository.findByLecture_LectureId(lectureId)
                .stream()
                .map(LectureSubjectResponse::new)
                .toList();
    }

    @Transactional
    public void updateSubject(Long subjectId, LectureSubjectUpdateRequest request) {
        LectureSubjectEntity subject = lectureSubjectRepository.findById(subjectId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_SUBJECT_NOT_FOUND));

        subject.changeSubjectTitle(request.getSubjectTitle()); // 캡슐화된 수정 메서드 사용
    }

    @Transactional
    public void deleteSubject(Long subjectId) {
        if (!lectureSubjectRepository.existsById(subjectId)) {
            throw new CustomAuthException(ErrorCode.LECTURE_SUBJECT_NOT_FOUND);
        }
        lectureSubjectRepository.deleteById(subjectId);
    }
}
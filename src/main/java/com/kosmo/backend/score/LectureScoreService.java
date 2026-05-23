package com.kosmo.backend.score;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import com.kosmo.backend.score.dto.LectureScoreCreateRequest;
import com.kosmo.backend.score.dto.LectureScoreResponse;
import com.kosmo.backend.score.dto.LectureScoreUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureScoreService {

    private final LectureScoreRepository lectureScoreRepository;
    private final LectureRepository lectureRepository;
    private final LecturePartRepository lecturePartRepository;

    @Transactional
    public void createLectureScore(LectureScoreCreateRequest request) {
        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        LecturePartEntity part = lecturePartRepository.findById(request.getLecturePartId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        LectureScoreEntity score = LectureScoreEntity.builder()
                .lecture(lecture)
                .lecturePart(part)
                .lectureProject(request.getLectureProject())
                .lectureProjectScore(request.getLectureProjectScore())
                .lectureProjectComment(request.getLectureProjectComment())
                .lectureProjectDay(request.getLectureProjectDay())
                .lectureProjectCategory(request.getLectureProjectCategory())
                .build();

        lectureScoreRepository.save(score);
    }

    public List<LectureScoreResponse> getScoresByLecturePartId(Long lecturePartId) {
        return lectureScoreRepository.findByLecturePart_LecturePartId(lecturePartId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LectureScoreResponse getScoreDetail(Long scoreId) {
        return lectureScoreRepository.findById(scoreId)
                .map(this::toDto)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SCORE_NOT_FOUND));
    }

    private LectureScoreResponse toDto(LectureScoreEntity entity) {
        return LectureScoreResponse.builder()
                .lectureScoreId(entity.getLectureScoreId())
                .lectureId(entity.getLecture().getLectureId())
                .lectureTitle(entity.getLecture().getLectureTitle())
                .lecturePartId(entity.getLecturePart().getLecturePartId())
                .lectureProject(entity.getLectureProject())
                .lectureProjectScore(entity.getLectureProjectScore())
                .lectureProjectComment(entity.getLectureProjectComment())
                .lectureProjectDay(entity.getLectureProjectDay())
                .lectureProjectCategory(entity.getLectureProjectCategory())
                .build();
    }

    @Transactional
    public void updateLectureScore(Long scoreId, LectureScoreUpdateRequest request) {
        LectureScoreEntity score = lectureScoreRepository.findById(scoreId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SCORE_NOT_FOUND));

        score.update(request);
    }

    @Transactional
    public void deleteLectureScore(Long scoreId) {
        LectureScoreEntity score = lectureScoreRepository.findById(scoreId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SCORE_NOT_FOUND));

        lectureScoreRepository.delete(score);
    }

}
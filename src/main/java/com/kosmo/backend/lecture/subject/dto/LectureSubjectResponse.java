package com.kosmo.backend.lecture.subject.dto;

import com.kosmo.backend.lecture.subject.LectureSubjectEntity;
import lombok.Getter;

@Getter
public class LectureSubjectResponse {
    private final Long subjectId;
    private final String subjectTitle;

    public LectureSubjectResponse(LectureSubjectEntity entity) {
        this.subjectId = entity.getSubjectId();
        this.subjectTitle = entity.getSubjectTitle();
    }
}
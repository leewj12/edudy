package com.kosmo.backend.lecture.subject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureSubjectUpdateRequest {
    private String subjectTitle; // 수정할 제목
}
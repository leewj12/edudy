package com.kosmo.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureSubjectUpdateDto {
    private Long subjectId;        // null이면 신규 등록
    private String subjectTitle;
}

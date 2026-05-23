package com.kosmo.backend.lecture.subject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureSubjectCreateRequest {
    private Long lectureId;         // 연결할 강의 ID
    private String subjectTitle;    // 과목명
}
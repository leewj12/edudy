package com.kosmo.backend.lecture.subject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LectureSubjectCreateListRequest {
    private Long lectureId;
    private List<String> subjectTitles; // 과목명 리스트
}
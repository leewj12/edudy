package com.kosmo.backend.lecture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LectureWithSubjectsCreateRequest {
    private LectureCreateRequest lecture;        // 기존 강의 등록 정보
    private List<String> subjectTitles;          // 과목명 리스트
}
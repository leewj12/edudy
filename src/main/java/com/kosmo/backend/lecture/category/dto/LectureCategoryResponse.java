package com.kosmo.backend.lecture.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureCategoryResponse {
    private Long lectureCategoryId;
    private String lectureCategoryName;
}

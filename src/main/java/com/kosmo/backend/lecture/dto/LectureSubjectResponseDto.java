package com.kosmo.backend.lecture.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureSubjectResponseDto {
    private Long subjectId;
    private String subjectTitle;
}

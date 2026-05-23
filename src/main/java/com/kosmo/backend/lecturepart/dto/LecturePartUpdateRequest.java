package com.kosmo.backend.lecturepart.dto;

import com.kosmo.backend.lecturepart.entity.LecturePartStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LecturePartUpdateRequest {
    private Boolean emp; // 취업 여부
    private LecturePartStatus status; // 상태 (대기, 제적, 수료중, 수료완료)
}
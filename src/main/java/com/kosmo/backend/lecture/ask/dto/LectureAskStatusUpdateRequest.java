package com.kosmo.backend.lecture.ask.dto;

import com.kosmo.backend.lecture.ask.AskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureAskStatusUpdateRequest {
    private AskStatus askStatus; // 대기, 승인, 반려
}

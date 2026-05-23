package com.kosmo.backend.lecture.ask.dto;

import com.kosmo.backend.lecture.ask.AskStatus;
import lombok.Data;

@Data
public class LectureAskCreateRequest {
    private Long lectureId;       // 어떤 강의에 대한 문의인지
    private String askName;
    private String askPhone;
    private Boolean askCard;
    private String askMemo;
    private AskStatus askStatus;  // 기본값은 WAITING 이겠지만, 필요 시 입력
}

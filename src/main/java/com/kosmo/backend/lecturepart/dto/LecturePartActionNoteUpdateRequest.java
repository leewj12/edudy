package com.kosmo.backend.lecturepart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LecturePartActionNoteUpdateRequest {
    private String actionNote; // "REQUESTED", "SENT_MESSAGE", "COMPLETED", "EXCLUDED"
}
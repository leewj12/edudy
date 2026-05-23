package com.kosmo.backend.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class AttendanceTimeRequest {
    private Long lectureAttId;       // 외출, 퇴실에 사용
    private Long lecturePartId;      // 입실에 사용
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;      // 공통 시간
}
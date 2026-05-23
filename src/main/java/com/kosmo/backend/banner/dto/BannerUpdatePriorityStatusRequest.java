package com.kosmo.backend.banner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BannerUpdatePriorityStatusRequest {
    private Long priority;        // 1~5 사이 값
    private Boolean status;       // true or false
    private LocalDateTime bannerStart;
    private LocalDateTime bannerEnd;
}
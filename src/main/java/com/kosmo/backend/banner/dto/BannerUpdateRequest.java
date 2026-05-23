package com.kosmo.backend.banner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BannerUpdateRequest {
    private String bannerContent;
    private Long bannerPriority;
    private Boolean bannerStatus;
    private LocalDateTime bannerStart;
    private LocalDateTime bannerEnd;
    private Long lectureId;
}
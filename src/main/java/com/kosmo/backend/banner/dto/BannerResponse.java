package com.kosmo.backend.banner.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BannerResponse {
    private Long bannerId;
    private String bannerImage;
    private String bannerContent;
    private Long bannerPriority;
    private Boolean bannerStatus;
    private LocalDateTime bannerStart;
    private LocalDateTime bannerEnd;
    private Long lectureId;
    private String lectureTitle;
}
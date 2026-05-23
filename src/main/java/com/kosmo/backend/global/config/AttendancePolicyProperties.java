package com.kosmo.backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "attendance.auto-mark-absent")
public class AttendancePolicyProperties {

    // 기본값 지정
    private boolean enabled;
    private int hour;
    private int minute;
    private boolean excludeWeekends;
    private boolean excludeHolidays;

    public String getCronExpression() {
        return String.format("0 %d %d * * ?", minute, hour);
    }
}
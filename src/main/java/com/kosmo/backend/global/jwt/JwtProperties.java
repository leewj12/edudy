package com.kosmo.backend.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, Long expiration) {}
//✅ 생성자 주입
//✅ 자동으로 getter, equals(), hashCode(), toString() 제공
//✅ final, 불변 객체 (변경 불가)

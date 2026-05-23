package com.kosmo.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

//로그인시 액세스 토큰 발급
@Getter
@AllArgsConstructor
public class LoginResponse {
    private final String accessToken;
}

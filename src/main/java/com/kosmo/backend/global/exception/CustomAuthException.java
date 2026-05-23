package com.kosmo.backend.global.exception;

import lombok.Getter;

@Getter
public class CustomAuthException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

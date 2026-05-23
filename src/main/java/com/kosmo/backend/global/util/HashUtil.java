package com.kosmo.backend.global.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    // SHA-256 해시 메서드
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // SHA-256 알고리즘
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8)); // 바이트 변환
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b); // 0xff: unsigned 처리
                if (hex.length() == 1) hexString.append('0'); // 한 자리면 앞에 0 붙임
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 해시 생성 실패", e);
        }
    }
}
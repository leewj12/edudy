package com.kosmo.backend.global.jwt;

import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWT 토큰 생성·검증 로직(JwtProvider) 단위 테스트.
 *
 * - DB가 전혀 필요 없다. JwtProvider는 비밀키만 있으면 동작하는 독립 모듈이다.
 * - 테스트용 비밀키(secret)와 만료시간(expiration)을 직접 만들어 JwtProvider를 생성한다.
 * - 핵심 검증: "토큰을 만든 뒤 다시 파싱하면 넣었던 정보가 그대로 나오는가" + "위조 토큰은 거부하는가"
 */
class JwtProviderTest {

    // HMAC-SHA256은 32바이트(256비트) 이상의 키가 필요하므로 넉넉히 긴 문자열 사용
    private static final String SECRET = "test-secret-key-for-jwt-unit-test-1234567890";
    private static final long EXPIRATION = 3600_000L; // 1시간

    private final JwtProvider jwtProvider =
            new JwtProvider(new JwtProperties(SECRET, EXPIRATION));

    /** 토큰 생성에 필요한 최소한의 사용자 객체를 만든다. (DB 저장 없이 메모리상 객체) */
    private UserEntity sampleUser() {
        return UserEntity.builder()
                .userId(1L)
                .userEmail("test@test.com")
                .usersName("홍길동")
                .userRole(Role.ADMIN)
                .userSign("sign-data")
                .build(); // lectureParts는 null → generateAccessToken이 null 처리함
    }

    @Test
    @DisplayName("토큰을 생성한 뒤 파싱하면 넣었던 이메일(subject)이 그대로 나온다")
    void generateAndExtractEmail() {
        String token = jwtProvider.generateAccessToken(sampleUser());

        String email = jwtProvider.getEmailFromToken(token);

        assertEquals("test@test.com", email);
    }

    @Test
    @DisplayName("토큰을 생성한 뒤 파싱하면 역할(role)이 ROLE_ADMIN으로 나온다")
    void generateAndExtractRole() {
        String token = jwtProvider.generateAccessToken(sampleUser());

        String role = jwtProvider.getRoleFromToken(token);

        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    @DisplayName("정상 발급된 토큰은 검증을 통과한다(true)")
    void validToken_passesValidation() {
        String token = jwtProvider.generateAccessToken(sampleUser());

        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("위조·손상된 토큰은 검증에 실패한다(false)")
    void tamperedToken_failsValidation() {
        String token = jwtProvider.generateAccessToken(sampleUser());

        // 토큰 뒷부분(서명)을 훼손 → 서명 불일치로 거부되어야 함
        String tampered = token + "abc";

        assertFalse(jwtProvider.validateToken(tampered));
    }
}

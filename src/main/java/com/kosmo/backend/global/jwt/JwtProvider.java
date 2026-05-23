package com.kosmo.backend.global.jwt;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey key;
    private final long expiration;

    public JwtProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
        this.expiration = jwtProperties.expiration();
    }


    // lecturePart, lecture 없을 경우 accessToken 에 안 넣음
//    // 토큰 생성
//    public String generateAccessToken(UserEntity user) {
//        Date now = new Date();
//
//        // 유저의 첫 번째 강의 정보 (하나만 넣는다고 가정)
//        LecturePartEntity lecturePart = user.getLectureParts().isEmpty() ? null : user.getLectureParts().get(0);
//        LectureEntity lecture = (lecturePart != null) ? lecturePart.getLecture() : null;
//
//        JwtBuilder builder = Jwts.builder()
//                .subject(user.getUserEmail())
//                .claim("userId", user.getUserId())
//                .claim("userName", user.getUsersName()) // ✅ userName 추가
//                .claim("role", user.getUserRole().getAuthority()) // ← "ROLE_ADMIN" 형식으로 저장
//                .issuedAt(now)
//                .expiration(new Date(now.getTime() + expiration))
//                .signWith(key);
//        // ✅ 강의 정보가 존재할 경우 추가
//        if (lecture != null) {
//            builder.claim("lectureId", lecture.getLectureId());
//            builder.claim("lectureTitle", lecture.getLectureTitle());
//        }
//        return builder.compact();
//    }

    public String generateAccessToken(UserEntity user) {
        Date now = new Date();

        // 기본 claims
        JwtBuilder builder = Jwts.builder()
                .subject(user.getUserEmail())
                .claim("userId", user.getUserId())
                .claim("userName", user.getUsersName())
                .claim("role", user.getUserRole().getAuthority())
                .claim("sign", user.getUserSign())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key);

        // lectureParts 중 첫 번째 강의가 있으면 lectureId, lectureTitle 추가
        if (user.getLectureParts() != null && !user.getLectureParts().isEmpty()) {
            LecturePartEntity part = user.getLectureParts().get(0);
            builder.claim("partId", part.getLecturePartId());

            LectureEntity lecture = part.getLecture();

            if (lecture != null) {
                builder.claim("lectureId", lecture.getLectureId());
                builder.claim("lectureTitle", lecture.getLectureTitle());
            } else {
                builder.claim("lectureId", null);
                builder.claim("lectureTitle", null);
            }
        } else {
            builder.claim("partId", null);
            builder.claim("lectureId", null);
            builder.claim("lectureTitle", null);
        }

        return builder.compact();
    }


    // 토큰에서 이메일(Subject) 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token); // 예외 없으면 유효
            return true;
        } catch (JwtException e) {
            System.out.println("토큰 오류: " + e.getMessage());
            return false;
        }
    }
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //요청헤드에서 토큰 가져오기
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값
        }
        return null;
    }

    // JWT에서 Role 꺼내기
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class); // "ROLE_ADMIN" 같은 문자열
    }

}

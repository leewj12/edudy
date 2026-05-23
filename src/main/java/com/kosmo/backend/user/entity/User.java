//package com.kosmo.backend.user.entity;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//// db쪽 연결 테이블
//
//@Entity
//@Table(name="User")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder(toBuilder = true)
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private String email;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role;
//
//    @Column(length = 500)
//    private String refreshToken;
//
//    @Column
//    private LocalDateTime refreshTokenExpiry;
//
//    // User.java
//
//    public void updateRefreshToken(String refreshToken, LocalDateTime expiry) {
//        this.refreshToken = refreshToken;
//        this.refreshTokenExpiry = expiry;
//    }
//
//    public void clearRefreshToken() {
//        this.refreshToken = null;
//        this.refreshTokenExpiry = null;
//    }
//
//
//}

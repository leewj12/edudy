package com.kosmo.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserInstructorDto {
    private Long userId;
    private String usersName;
    private String userEmail;   // ✅ 추가
    private LocalDate userBirth; // ✅ 추가
    private String userPhone;   // ✅ 추가
}
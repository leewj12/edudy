package com.kosmo.backend.user.dto;


import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.SnsType;
import com.kosmo.backend.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//회원가입시
@Getter
@Setter
@Builder
public class SignupRequest {

    private String email;
    private String password;
    private String name;
    private LocalDate birth;
    private String birthBack;

    private String phone;
    private String postcode;
    private String address;
    private String addressDetail;

    private String grade;
    private Boolean marketing;
    private Boolean privacy;
    private SnsType sns;

    private Role role;
    private UserStatus status;
}
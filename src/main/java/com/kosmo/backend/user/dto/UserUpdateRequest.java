package com.kosmo.backend.user.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String userEmail;
    private String userPwd;              // 비밀번호
    private String userPhone;            // 전화번호

    private String userPostcode;         // 우편번호
    private String userAddress;          // 기본 주소
    private String userAddressDetail;    // 상세 주소

    private String userGrade;            // 학력 혹은 교육 레벨
    private String userThumbnail;        // 썸네일 (프로필 사진)

    private Boolean userMarketing;       // 마케팅 수신 동의
}
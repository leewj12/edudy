package com.kosmo.backend.user.dto;


import com.kosmo.backend.user.entity.SnsType;
import com.kosmo.backend.user.entity.UserStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateBackDTO {

    private String userName;             // 이름
    private String userGender;           // 성별
    private LocalDate userBirth;         // 생년월일
    private String userBirthBack;        // 주민번호 뒷자리

    private String userPhone;            // 전화번호
    private String userPostcode;         // 우편번호
    private String userAddress;          // 기본 주소
    private String userAddressDetail;    // 상세 주소

    private String userGrade;            // 학력 혹은 교육 레벨
    private SnsType userSns;              // SNS ID
    private String userThumbnail;        // 썸네일 (프로필 사진)

    private Boolean userMarketing;       // 마케팅 수신 동의
    private Boolean userPrivacy;         // 개인정보 수집 동의

    private UserStatus userStatus;       // 상태 (ACTIVE / INACTIVE)
}
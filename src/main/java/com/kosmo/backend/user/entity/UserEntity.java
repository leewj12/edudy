package com.kosmo.backend.user.entity;

import com.kosmo.backend.consult.LectureConsultEntity;
import com.kosmo.backend.lecture.staff.LectureStaffEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.train.instrwbs.InstrWbsTimeEntity;
import com.kosmo.backend.train.traintime.LectureTrainTimeEntity;
import com.kosmo.backend.user.dto.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "user_pwd", nullable = false)
    private String userPwd;

    @Column(name = "user_name", nullable = false)
    private String usersName;

    @Column(name = "user_gender", nullable = false)
    private String userGender;

    @Column(name = "user_birth", nullable = false)
    private LocalDate userBirth;

    @Column(name = "user_birth_back", nullable = false)
    private String userBirthBack;

    @Column(name = "user_phone", nullable = false, unique = true)
    private String userPhone;

    @Column(name = "user_postcode")
    private String userPostcode;

    @Column(name = "user_address")
    private String userAddress;

    @Column(name = "user_address_detail")
    private String userAddressDetail;

    @Column(name = "user_grade", nullable = false)
    private String userGrade;

    @Column(name = "user_marketing", nullable = false)
    private Boolean userMarketing;

    @Column(name = "user_privacy", nullable = false)
    private Boolean userPrivacy;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_sns", nullable = false)
    private SnsType userSns;

    @Column(name = "user_thumbnail")
    private String userThumbnail;

    @Column(name = "user_sign")
    private String userSign;

    @Column(name = "user_last_login")
    private LocalDateTime userLastLogin;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role userRole;

    @CreatedDate
    @Column(name = "user_created_at", nullable = false, updatable = false)
    private LocalDateTime userCreatedAt;

    @LastModifiedDate
    @Column(name = "user_updated_at")
    private LocalDateTime userUpdatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoginLogEntity> loginLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LectureStaffEntity> lectureStaffs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LecturePartEntity> lectureParts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureConsultEntity> lectureConsults = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstrWbsTimeEntity> wbsList = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTrainTimeEntity> lectureTrainTimeList = new ArrayList<>();


    // JWT 리프레시 토큰 관련 유틸
    public void updateRefreshToken(String refreshToken, LocalDateTime expiry) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiry = expiry;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiry = null;
    }

    // Spring Security - 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.userRole.name()));
    }

    @Override
    public String getPassword() {
        return this.userPwd;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.userStatus == UserStatus.ACTIVE;
    }

    // 회원 정보 수정
    public void updateUserInfo(UserUpdateRequest dto) {
        if (dto.getUserEmail() != null) this.userEmail = dto.getUserEmail();
        if (dto.getUserPhone() != null) this.userPhone = dto.getUserPhone();
        if (dto.getUserPostcode() != null) this.userPostcode = dto.getUserPostcode();
        if (dto.getUserAddress() != null) this.userAddress = dto.getUserAddress();
        if (dto.getUserAddressDetail() != null) this.userAddressDetail = dto.getUserAddressDetail();
        if (dto.getUserGrade() != null) this.userGrade = dto.getUserGrade();
        if (dto.getUserThumbnail() != null) this.userThumbnail = dto.getUserThumbnail();
        if (dto.getUserMarketing() != null) this.userMarketing = dto.getUserMarketing();
        this.userUpdatedAt = LocalDateTime.now();
    }

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.userPwd = encodedPassword;
        this.userUpdatedAt = LocalDateTime.now();
    }

    // 로그인 성공 처리
    public void loginSuccess() {
        this.userLastLogin = LocalDateTime.now();
    }

    // 계정 비활성화
    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
        this.userUpdatedAt = LocalDateTime.now();
    }

    // 예: 로그인 시, userStatus가 DELETED인 경우 차단
    public void updateStatus(UserStatus status) {
        this.userStatus = status;
    }
    // 계속 getUserName을 안먹어서 직접 넣음 (UserDetails에 getUsername이 있음)
//    public String getUserName() {
//        return userName;
//    }

    public void updateRole(Role newRole) {
        this.userRole = newRole;
    }

    public void updateUserSign(String sign) {
        this.userSign = sign;
        this.userUpdatedAt = LocalDateTime.now();
    }

}

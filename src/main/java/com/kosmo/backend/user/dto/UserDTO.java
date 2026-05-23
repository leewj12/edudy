package com.kosmo.backend.user.dto;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

//유저 정보 조회시
@Getter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String name;
    private String gender;
    private String birth;
    private String phone;
    private String address;
    private String addressDetail;
    private String postcode;
    private String thumbnail;
    private String sns;
    private Boolean marketing;
    private Boolean privacy;       // ✅ 추가
    private String grade;          // ✅ 추가
    private String status;         // ✅ 추가
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // ✅ 추가
    private Long lectureId;
    private String lectureTitle;

    public UserDTO(UserEntity user) {
        this.id = user.getUserId();
        this.email = user.getUserEmail();
        this.role = user.getUserRole().getAuthority();
        this.name = user.getUsersName();
        this.gender = user.getUserGender();
        this.birth = user.getUserBirth() + "-" + user.getUserBirthBack();
        this.phone = user.getUserPhone();
        this.address = user.getUserAddress();
        this.addressDetail = user.getUserAddressDetail();
        this.postcode = user.getUserPostcode();
        this.thumbnail = user.getUserThumbnail();
        this.sns = user.getUserSns().name();
        this.marketing = user.getUserMarketing();
        this.privacy = user.getUserPrivacy();            // ✅
        this.grade = user.getUserGrade();                // ✅
        this.status = user.getUserStatus().name();       // ✅
        this.createdAt = user.getUserCreatedAt();
        this.lastLogin = user.getUserLastLogin();

        // ✅ 수강 중인 첫 강의 정보 (Optional)
        if (user.getLectureParts() != null && !user.getLectureParts().isEmpty()) {
            LecturePartEntity part = user.getLectureParts().get(0); // 가장 첫 강의
            LectureEntity lecture = part.getLecture();
            if (lecture != null) {
                this.lectureId = lecture.getLectureId();
                this.lectureTitle = lecture.getLectureTitle();
            }
        }
    }
}
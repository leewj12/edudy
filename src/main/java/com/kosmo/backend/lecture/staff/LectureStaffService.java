package com.kosmo.backend.lecture.staff;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecture.staff.dto.LectureStaffCreateRequest;
import com.kosmo.backend.lecture.staff.dto.LectureStaffResponse;
import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.UserEntity;
import com.kosmo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureStaffService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureStaffRepository lectureStaffRepository;

    public void createLectureStaff(LectureStaffCreateRequest request) {
        UserEntity instructor = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));

        if (instructor.getUserRole() != Role.INSTRUCTOR) {
            throw new CustomAuthException(ErrorCode.NOT_INSTRUCTOR);
        }

        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        // 이미 등록되어 있는지 중복 체크
        boolean alreadyExists = lectureStaffRepository.existsByUserAndLecture(instructor, lecture);
        if (alreadyExists) {
            throw new CustomAuthException(ErrorCode.ALREADY_ASSIGNED);
        }

        LectureStaffEntity newStaff = LectureStaffEntity.builder()
                .user(instructor)
                .lecture(lecture)
                .build();

        lectureStaffRepository.save(newStaff);
    }


    public List<LectureStaffResponse> getAllLectureStaffs() {
        List<LectureStaffEntity> staffList = lectureStaffRepository.findAll();

        return staffList.stream()
                .map(staff -> LectureStaffResponse.builder()
                        .lectureStaffId(staff.getLectureStaffId())
                        .userId(staff.getUser().getUserId())
                        .userName(staff.getUser().getUsersName())
                        .userEmail(staff.getUser().getUserEmail())
                        .lectureId(staff.getLecture().getLectureId())
                        .lectureTitle(staff.getLecture().getLectureTitle())
                        .build())
                .collect(Collectors.toList());
    }

    public List<LectureStaffResponse> getLectureStaffsByLectureId(Long lectureId) {
        List<LectureStaffEntity> staffList = lectureStaffRepository.findByLecture_LectureId(lectureId);

        return staffList.stream()
                .map(staff -> LectureStaffResponse.builder()
                        .lectureStaffId(staff.getLectureStaffId())
                        .userId(staff.getUser().getUserId())
                        .userName(staff.getUser().getUsersName())
                        .userEmail(staff.getUser().getUserEmail())
                        .userBirth(staff.getUser().getUserBirth())
                        .userPhone(staff.getUser().getUserPhone())
                        .lectureId(staff.getLecture().getLectureId())
                        .lectureTitle(staff.getLecture().getLectureTitle())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteLectureStaff(Long lectureStaffId) {
        LectureStaffEntity staff = lectureStaffRepository.findById(lectureStaffId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_STAFF_NOT_FOUND));
        lectureStaffRepository.delete(staff);
    }
}
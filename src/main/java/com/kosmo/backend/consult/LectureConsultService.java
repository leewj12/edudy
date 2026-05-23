package com.kosmo.backend.consult;

import com.kosmo.backend.consult.dto.KeywordCountResponse;
import com.kosmo.backend.consult.dto.LectureConsultCreateRequest;
import com.kosmo.backend.consult.dto.LectureConsultResponse;
import com.kosmo.backend.consult.dto.LectureConsultUpdateRequest;
import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.staff.LectureStaffRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import com.kosmo.backend.user.entity.UserEntity;
import com.kosmo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureConsultService {

    private final LectureConsultRepository lectureConsultRepository;
    private final LectureStaffRepository lectureStaffRepository;
    private final LecturePartRepository lecturePartRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createLectureConsult(LectureConsultCreateRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));

        LecturePartEntity part = lecturePartRepository.findById(request.getLecturePartId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        LectureConsultEntity consult = LectureConsultEntity.builder()
                .user(user)  // 🔁 LectureStaff 대신 User 직접 바인딩
                .lecturePart(part)
                .consultTitle(request.getConsultTitle())
                .consultDate(request.getConsultDate())
                .consultContent(request.getConsultContent())
                .consultSpecial(request.getConsultSpecial())
                .consultType(request.getConsultType())
                .consultKeyword(request.getConsultKeyword())
                .build();

        lectureConsultRepository.save(consult);
    }

    public List<LectureConsultResponse> getAllConsults() {
        return lectureConsultRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<LectureConsultResponse> getConsultsByLectureId(Long lectureId) {
//        List<LectureConsultEntity> entities = lectureConsultRepository.findByLecturePart_Lecture_LectureId(lectureId);
        List<LectureConsultEntity> entities = lectureConsultRepository.findByLecturePart_Lecture_LectureIdOrderByConsultDateDesc(lectureId);

        return entities.stream()
                .map(this::toDto) // 또는 .of(entity)
                .toList();
    }

    public LectureConsultResponse getConsultDetail(Long id) {
        LectureConsultEntity consult = lectureConsultRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CONSULT_NOT_FOUND));
        return toDto(consult);
    }

    private LectureConsultResponse toDto(LectureConsultEntity entity) {
        return LectureConsultResponse.builder()
                .lectureConsultId(entity.getLectureConsultId())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getUsersName())
                .lecturePartId(entity.getLecturePart().getLecturePartId())
                .lecturePartName(entity.getLecturePart().getUser().getUsersName())
                .consultTitle(entity.getConsultTitle())
                .consultDate(entity.getConsultDate())
                .consultContent(entity.getConsultContent())
                .consultSpecial(entity.getConsultSpecial())
                .consultType(entity.getConsultType())
                .consultKeyword(entity.getConsultKeyword())
                .consultCreatedAt(entity.getConsultCreatedAt())
                .consultUpdatedAt(entity.getConsultUpdatedAt())
                .build();
    }

    @Transactional
    public void updateLectureConsult(Long consultId, LectureConsultUpdateRequest request) {
        LectureConsultEntity consult = lectureConsultRepository.findById(consultId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CONSULT_NOT_FOUND));

        // 변경 감지 방식으로 수정
        consult.updateConsult(request);
    }

    @Transactional
    public void deleteLectureConsult(Long consultId) {
        LectureConsultEntity consult = lectureConsultRepository.findById(consultId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CONSULT_NOT_FOUND));

        lectureConsultRepository.delete(consult);
    }

    public List<KeywordCountResponse> getKeywordStatistics() {
        return lectureConsultRepository.countConsultsByKeyword();
    }
}
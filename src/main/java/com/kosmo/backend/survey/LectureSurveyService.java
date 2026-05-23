package com.kosmo.backend.survey;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecture.staff.LectureStaffEntity;
import com.kosmo.backend.lecture.staff.LectureStaffRepository;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.lecturepart.repository.LecturePartRepository;
import com.kosmo.backend.survey.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureSurveyService {

    private final LectureRepository lectureRepository;
    private final LecturePartRepository lecturePartRepository;
    private final LectureStaffRepository lectureStaffRepository;
    private final LectureSurveyRepository lectureSurveyRepository;
    private final InstrSurveyRepository instrSurveyRepository;

    @Transactional
    public void createSurveyForAllLectureParts(Long lectureId, AdminSurveyCreateRequest request) {
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        List<LecturePartEntity> parts = lecturePartRepository.findAllByLecture_LectureId(lectureId);

        for (LecturePartEntity part : parts) {
            LectureSurveyEntity survey = lectureSurveyRepository.save(
                    LectureSurveyEntity.builder()
                            .lecturePart(part)
                            .lectureSurveyTitle(request.getLectureSurveyTitle())
                            .lectureQuestion1(request.getLectureQuestion1())
                            .lectureQuestion2(request.getLectureQuestion2())
                            .lectureQuestion3(request.getLectureQuestion3())
                            .build()
            );

            // 강사 설문 저장
            for (AdminSurveyCreateRequest.InstrSurveyCreateRequest instrReq : request.getInstrSurveys()) {
                LectureStaffEntity staff = lectureStaffRepository.findById(instrReq.getLectureStaffId())
                        .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_STAFF_NOT_FOUND));

                InstrSurveyEntity instr = InstrSurveyEntity.builder()
                        .lectureSurvey(survey)
                        .lectureStaff(staff)
                        .instrQuestion1(instrReq.getInstrQuestion1())
                        .instrQuestion2(instrReq.getInstrQuestion2())
                        .instrQuestion3(instrReq.getInstrQuestion3())
                        .build();

                instrSurveyRepository.save(instr);
            }
        }
    }
    @Transactional
    public void updateSurveyQuestionsOnly(Long lectureSurveyId, AdminSurveyUpdateRequest request) {
        LectureSurveyEntity survey = lectureSurveyRepository.findById(lectureSurveyId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

        // 과정 설문 질문 수정 (답변은 건드리지 않음)
        survey.updateLectureSurvey(
                request.getLectureSurveyTitle(),
                request.getLectureQuestion1(),
                request.getLectureQuestion2(),
                request.getLectureQuestion3(),
                survey.getLectureAnswer1(),  // 기존 답변 유지
                survey.getLectureAnswer2(),
                survey.getLectureAnswer3()
        );

        for (AdminSurveyUpdateRequest.InstrSurveyQuestionUpdateRequest instrReq : request.getInstrSurveys()) {
            InstrSurveyEntity instr = instrSurveyRepository.findById(instrReq.getInstrSurveyId())
                    .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

            instr.updateInstrSurvey(
                    instrReq.getInstrQuestion1(),
                    instrReq.getInstrQuestion2(),
                    instrReq.getInstrQuestion3(),
                    instr.getInstrAnswer1(), // 기존 답변 유지
                    instr.getInstrAnswer2(),
                    instr.getInstrAnswer3()
            );
        }
    }

    public List<LectureSurveyResponse> getSurveysByLecture(Long lectureId) {
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        List<LecturePartEntity> parts = lecturePartRepository.findAllByLecture_LectureId(lectureId);

        return parts.stream()
                .flatMap(part -> lectureSurveyRepository.findAllByLecturePart(part).stream())
                .map(this::toResponseDto)
                .toList();
    }


//    @Transactional
//    public void createSurveyWithInstrs(LectureSurveyRequest request) {
//        LecturePartEntity lecturePart = lecturePartRepository.findById(request.getLecturePartId())
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));
//
//        // 1. 과정 설문 저장
//        LectureSurveyEntity lectureSurvey = lectureSurveyRepository.save(
//                LectureSurveyEntity.builder()
//                        .lecturePart(lecturePart)
//                        .lectureSurveyTitle(request.getLectureSurveyTitle())
//                        .lectureQuestion1(request.getLectureQuestion1())
//                        .lectureQuestion2(request.getLectureQuestion2())
//                        .lectureQuestion3(request.getLectureQuestion3())
//                        .lectureAnswer1(request.getLectureAnswer1())
//                        .lectureAnswer2(request.getLectureAnswer2())
//                        .lectureAnswer3(request.getLectureAnswer3())
//                        .build()
//        );
//
//        // 2. 강사 설문 저장
//        for (InstrSurveyRequest instrReq : request.getInstrSurveys()) {
//            LectureStaffEntity staff = lectureStaffRepository.findById(instrReq.getLectureStaffId())
//                    .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_STAFF_NOT_FOUND));
//
//            InstrSurveyEntity instrSurvey = InstrSurveyEntity.builder()
//                    .lectureSurvey(lectureSurvey) // 이거 누락되면 외래키 에러 납니다
//                    .lectureStaff(staff)
//                    .instrQuestion1(instrReq.getInstrQuestion1())
//                    .instrQuestion2(instrReq.getInstrQuestion2())
//                    .instrQuestion3(instrReq.getInstrQuestion3())
//                    .instrAnswer1(instrReq.getInstrAnswer1())
//                    .instrAnswer2(instrReq.getInstrAnswer2())
//                    .instrAnswer3(instrReq.getInstrAnswer3())
//                    .build();
//
//            instrSurveyRepository.save(instrSurvey);
//        }
//    }

    public List<LectureSurveyResponse> getSurveysByLecturePart(Long lecturePartId) {
        LecturePartEntity lecturePart = lecturePartRepository.findById(lecturePartId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_PART_NOT_FOUND));

        List<LectureSurveyEntity> surveys = lectureSurveyRepository.findAllByLecturePart(lecturePart);

        return surveys.stream().map(this::toResponseDto).toList();
    }

    private LectureSurveyResponse toResponseDto(LectureSurveyEntity entity) {
        return LectureSurveyResponse.builder()
                .lectureSurveyId(entity.getLectureSurveyId())
                .lecturePartId(entity.getLecturePart().getLecturePartId())
                .lecturePartName(entity.getLecturePart().getUser().getUsersName()) // 수강생 이름
                .lectureSurveyTitle(entity.getLectureSurveyTitle())
                .lectureQuestion1(entity.getLectureQuestion1())
                .lectureQuestion2(entity.getLectureQuestion2())
                .lectureQuestion3(entity.getLectureQuestion3())
                .lectureAnswer1(entity.getLectureAnswer1())
                .lectureAnswer2(entity.getLectureAnswer2())
                .lectureAnswer3(entity.getLectureAnswer3())
                .lectureSurveyCreatedAt(entity.getLectureSurveyCreatedAt())
                .build();
    }

    public LectureSurveyDetailResponse getSurveyDetail(Long surveyId) {
        LectureSurveyEntity survey = lectureSurveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

        List<InstrSurveyEntity> instrSurveys = instrSurveyRepository.findAllByLectureSurvey(survey);

        List<InstrSurveyResponse> instrDtos = instrSurveys.stream().map(instr -> InstrSurveyResponse.builder()
                .instrSurveyId(instr.getInstrSurveyId())
                .lectureStaffId(instr.getLectureStaff().getLectureStaffId())
                .instrQuestion1(instr.getInstrQuestion1())
                .instrQuestion2(instr.getInstrQuestion2())
                .instrQuestion3(instr.getInstrQuestion3())
                .instrAnswer1(instr.getInstrAnswer1())
                .instrAnswer2(instr.getInstrAnswer2())
                .instrAnswer3(instr.getInstrAnswer3())
                .build()).toList();

        return LectureSurveyDetailResponse.builder()
                .lectureSurveyId(survey.getLectureSurveyId())
                .lectureSurveyTitle(survey.getLectureSurveyTitle())
                .lectureQuestion1(survey.getLectureQuestion1())
                .lectureQuestion2(survey.getLectureQuestion2())
                .lectureQuestion3(survey.getLectureQuestion3())
                .lectureAnswer1(survey.getLectureAnswer1())
                .lectureAnswer2(survey.getLectureAnswer2())
                .lectureAnswer3(survey.getLectureAnswer3())
                .lectureSurveyCreatedAt(survey.getLectureSurveyCreatedAt())
                .instrSurveys(instrDtos)
                .build();
    }

//    @Transactional
//    public void updateSurveyWithInstrs(LectureSurveyUpdateRequest request) {
//        LectureSurveyEntity survey = lectureSurveyRepository.findById(request.getLectureSurveyId())
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));
//
//        // 과정 설문 수정 (setter 또는 메서드 이용)
//        survey.updateLectureSurvey(
//                request.getLectureSurveyTitle(),
//                request.getLectureQuestion1(),
//                request.getLectureQuestion2(),
//                request.getLectureQuestion3(),
//                request.getLectureAnswer1(),
//                request.getLectureAnswer2(),
//                request.getLectureAnswer3()
//        );
//
//        // 강사 설문 수정
//        for (InstrSurveyUpdateRequest instrReq : request.getInstrSurveys()) {
//            InstrSurveyEntity instr = instrSurveyRepository.findById(instrReq.getInstrSurveyId())
//                    .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));
//
//            instr.updateInstrSurvey(
//                    instrReq.getInstrQuestion1(),
//                    instrReq.getInstrQuestion2(),
//                    instrReq.getInstrQuestion3(),
//                    instrReq.getInstrAnswer1(),
//                    instrReq.getInstrAnswer2(),
//                    instrReq.getInstrAnswer3()
//            );
//        }
//    }

    @Transactional
    public void updateAnswersOnly(Long lectureSurveyId, LectureSurveyUpdateAnswerRequest request) {
        LectureSurveyEntity survey = lectureSurveyRepository.findById(lectureSurveyId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

        survey.updateAnswers(
                request.getLectureAnswer1(),
                request.getLectureAnswer2(),
                request.getLectureAnswer3()
        );

        for (InstrSurveyAnswerUpdateRequest instrReq : request.getInstrSurveys()) {
            InstrSurveyEntity instr = instrSurveyRepository.findById(instrReq.getInstrSurveyId())
                    .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

            instr.updateAnswers(
                    instrReq.getInstrAnswer1(),
                    instrReq.getInstrAnswer2(),
                    instrReq.getInstrAnswer3()
            );
        }
    }


    @Transactional
    public void deleteSurvey(Long lectureSurveyId) {
        LectureSurveyEntity survey = lectureSurveyRepository.findById(lectureSurveyId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.SURVEY_NOT_FOUND));

        lectureSurveyRepository.delete(survey); // 강사 설문도 같이 삭제됨
    }
}
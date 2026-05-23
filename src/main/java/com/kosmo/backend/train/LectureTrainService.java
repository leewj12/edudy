package com.kosmo.backend.train;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.train.dto.LectureTrainCreateRequest;
import com.kosmo.backend.train.dto.LectureTrainResponse;
import com.kosmo.backend.train.instrwbs.InstrWbsTimeRepository;
import com.kosmo.backend.train.traintime.LectureTrainTimeEntity;
import com.kosmo.backend.train.traintime.LectureTrainTimeRepository;
import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.UserEntity;
import com.kosmo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureTrainService {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final LectureTrainRepository lectureTrainRepository;
    private final InstrWbsTimeRepository instrWbsTimeRepository;
    private final LectureTrainTimeRepository lectureTrainTimeRepository;


    @Transactional
    public void createLectureTrain(LectureTrainCreateRequest request) {
        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        LectureTrainEntity train = LectureTrainEntity.builder()
                .lecture(lecture)
                .trainDate(request.getTrainDate())
                .trainSpecial(request.getTrainSpecial())

                // ✅ 추가된 필드 적용
                .trainAbsentees(request.getTrainAbsentees())
                .trainEarlyLeavers(request.getTrainEarlyLeavers())
                .trainLatecomers(request.getTrainLatecomers())
                .trainOutingStudents(request.getTrainOutingStudents())
                .trainInstrSign(request.getTrainInstrSign())
                .trainAdminSign(request.getTrainAdminSign())
                .build();

        List<LectureTrainTimeEntity> times = request.getTimeRequests().stream()
                .map(time -> {
                    UserEntity instructor = null;

                    // userId가 있을 경우만 조회
                    if (time.getUserId() != null) {
                        instructor = userRepository.findById(time.getUserId())
                                .filter(u -> u.getUserRole() == Role.INSTRUCTOR)
                                .orElseThrow(() -> new CustomAuthException(ErrorCode.NOT_INSTRUCTOR));
                    }
//                    // ✅ 강사의 해당 시간대 예약 여부 확인
//                    InstrWbsTimeEntity wbsTime = instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                            instructor, request.getTrainDate(), time.getLectureTime()
//                    ).orElseThrow(() -> new CustomAuthException(ErrorCode.WBS_TIME_NOT_FOUND));
//
//                    if (wbsTime.isAvailable()) {
//                        throw new CustomAuthException(ErrorCode.WBS_ALREADY_BOOKED); // 이미 사용중
//                    }
//
//                    // ✅ 사용가능한 경우 true로 바꾸고 저장 예정
//                    wbsTime.updateAvailability(true); // 아래에서 정의

                    return LectureTrainTimeEntity.builder()
                            .lectureTrain(train)
                            .instructor(instructor)
                            .lectureTime(time.getLectureTime())
                            .trainTitle(time.getTrainTitle())
                            .trainContent(time.getTrainContent())
                            .build();
                }).toList();

        train.getTrainTimeList().addAll(times);

//        // WBS 시간대 저장
//        instrWbsTimeRepository.saveAll(
//                times.stream().map(t -> {
//                    return instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                            t.getInstructor(), request.getTrainDate(), t.getLectureTime()
//                    ).get(); // 이미 위에서 조회했기 때문에 get() 사용
//                }).toList()
//        );

        lectureTrainRepository.save(train);
    }

    // ✅ 훈련일지 리스트
    public List<LectureTrainResponse> getLectureTrainList(Long lectureId) {
        List<LectureTrainEntity> trains = lectureTrainRepository.findByLecture_LectureIdOrderByTrainDateDesc(lectureId);
        return trains.stream()
                .map(LectureTrainResponse::fromEntity)
                .toList();
    }

    // ✅ 훈련일지 상세
    public LectureTrainResponse getLectureTrainDetail(Long trainId) {
        LectureTrainEntity train = lectureTrainRepository.findById(trainId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.TRAIN_NOT_FOUND));
        return LectureTrainResponse.fromEntity(train);
    }

    @Transactional
    public void updateLectureTrain(Long trainId, LectureTrainCreateRequest request) {
        LectureTrainEntity train = lectureTrainRepository.findById(trainId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.TRAIN_NOT_FOUND));

        LectureEntity lecture = lectureRepository.findById(request.getLectureId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

//        // 🔁 1. 기존 예약된 WBS 시간대를 false 처리 및 trainTime 삭제
//        for (LectureTrainTimeEntity oldTime : train.getTrainTimeList()) {
//            InstrWbsTimeEntity oldWbs = instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                    oldTime.getInstructor(), train.getTrainDate(), oldTime.getLectureTime()
//            ).orElseThrow(() -> new CustomAuthException(ErrorCode.WBS_TIME_NOT_FOUND));
//
//            oldWbs.updateAvailability(false); // 다시 비워줌
//            lectureTrainTimeRepository.delete(oldTime); // 기존 시간대 삭제
//        }
        // 🔁 1. 기존 시간대 삭제
        lectureTrainTimeRepository.deleteAll(train.getTrainTimeList()); // 기존 시간대 일괄 삭제

        train.getTrainTimeList().clear();

        // 🔁 2. 새로 들어온 시간대들 다시 저장
        List<LectureTrainTimeEntity> newTimes = request.getTimeRequests().stream()
                .map(time -> {
                    UserEntity instructor = null;

                    // userId가 있을 경우만 조회
                    if (time.getUserId() != null) {
                        instructor = userRepository.findById(time.getUserId())
                                .filter(u -> u.getUserRole() == Role.INSTRUCTOR)
                                .orElseThrow(() -> new CustomAuthException(ErrorCode.NOT_INSTRUCTOR));
                    }
//                    InstrWbsTimeEntity wbsTime = instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                            instructor, request.getTrainDate(), time.getLectureTime()
//                    ).orElseThrow(() -> new CustomAuthException(ErrorCode.WBS_TIME_NOT_FOUND));
//
//                    if (wbsTime.isAvailable()) {
//                        throw new CustomAuthException(ErrorCode.WBS_ALREADY_BOOKED);
//                    }
//
//                    wbsTime.updateAvailability(true);

                    return LectureTrainTimeEntity.builder()
                            .lectureTrain(train)
                            .instructor(instructor)
                            .lectureTime(time.getLectureTime())
                            .trainTitle(time.getTrainTitle())
                            .trainContent(time.getTrainContent())
                            .build();
                }).toList();

        train.getTrainTimeList().addAll(newTimes);

//        // 🔁 3. WBS 저장
//        instrWbsTimeRepository.saveAll(
//                newTimes.stream().map(t -> instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                        t.getInstructor(), request.getTrainDate(), t.getLectureTime()
//                ).get()).toList()
//        );

        // 🔁 4. 훈련일지 본체 수정
        train.updateLectureTrain(
                lecture,
                request.getTrainDate(),
                request.getTrainSpecial(),
                request.getTrainAbsentees(),
                request.getTrainLatecomers(),
                request.getTrainEarlyLeavers(),
                request.getTrainOutingStudents(),
                request.getTrainInstrSign(),
                request.getTrainAdminSign()
        );

        // 저장은 Dirty Checking
    }

    @Transactional
    public void deleteLectureTrain(Long trainId) {
        LectureTrainEntity train = lectureTrainRepository.findById(trainId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.TRAIN_NOT_FOUND));

        // 관련된 시간대 엔티티 먼저 삭제 (Cascade 설정이 없다면 직접 삭제해야 함)
        lectureTrainTimeRepository.deleteByLectureTrain(train);

//        // WBS 시간대도 사용 가능하도록 초기화 (필요하다면)
//        train.getTrainTimeList().forEach(trainTime -> {
//            InstrWbsTimeEntity wbs = instrWbsTimeRepository.findByInstructorAndWbsDateAndWbsTime(
//                    trainTime.getInstructor(), train.getTrainDate(), trainTime.getLectureTime()
//            ).orElse(null);
//            if (wbs != null) {
//                wbs.updateAvailability(false); // 다시 사용 가능하게 설정
//            }
//        });

        lectureTrainRepository.delete(train); // 훈련일지 삭제
    }


}

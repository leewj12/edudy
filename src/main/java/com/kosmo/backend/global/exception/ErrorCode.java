package com.kosmo.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련
    USER_DELETED(HttpStatus.FORBIDDEN, "삭제된 사용자입니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "비활성화된 계정입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    // 회원가입 관련
    INVALID_BIRTH_BACK(HttpStatus.BAD_REQUEST, "주민번호 뒷자리가 유효하지 않습니다."),
    GENDER_INFERENCE_FAILED(HttpStatus.BAD_REQUEST, "주민번호를 통해 성별을 판단할 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    USER_PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 등록된 휴대폰 번호입니다."),

    // 계정 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    USER_PHONE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 휴대폰 번호로 가입된 회원이 없습니다."),
    UNAUTHORIZED_ROLE(HttpStatus.FORBIDDEN,"접근 권한이 없습니다."),
    NOT_INSTRUCTOR(HttpStatus.BAD_REQUEST, "해당 사용자는 강사 권한이 아닙니다."),

    // 유효성 검증
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),

    // LectureAsk 관련
    ALREADY_ASKED(HttpStatus.BAD_REQUEST, "이미 신청한 수강 내역이 존재합니다."),

    // Lecture 관련
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 강의가 존재하지 않습니다."),
    LECTURE_ASK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 문의가 존재하지 않습니다."),
    LECTURE_SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 강의 과목이 존재하지 않습니다."),
    LECTURE_ASK_ALREADY_HANDLED(HttpStatus.BAD_REQUEST, "이미 처리된 신청입니다."),

    // LectureCategory 관련
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    CATEGORY_HAS_LECTURES(HttpStatus.BAD_REQUEST, "카테고리에 속한 강의가 존재하므로 삭제할 수 없습니다."),

    // LecturePart 관련
    LECTURE_PART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 강의 참가자가 존재하지 않습니다."),
    INVALID_ACTION_NOTE(HttpStatus.BAD_REQUEST, "유효하지 않은 상담 상태 값입니다."),  // ✅ 추가

    // LectureStaff 관련
    ALREADY_ASSIGNED(HttpStatus.CONFLICT, "이미 해당 강의에 등록된 강사입니다."),
    LECTURE_STAFF_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 강의 담당자를 찾을 수 없습니다."),

    // 출석 관련
    ATTENDANCE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 출석이 기록되었습니다."),
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "출석 정보가 존재하지 않습니다."),
    INVALID_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "해당 출석 상태로 변경할 수 없습니다."),
    RECOGNIZED_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST, "출석 인정 상태는 수정할 수 없습니다."),
    RECOGNIZED_ALREADY(HttpStatus.BAD_REQUEST, "이미 출석 인정된 항목입니다."),

    // GPS 관련
    INVALID_GPS_LOCATION(HttpStatus.BAD_REQUEST, "GPS 위치가 출석 허용 범위를 벗어났습니다."),

    // 상담 관련
    CONSULT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상담일지를 찾을 수 없습니다."),

    // 성적 관련
    SCORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 성적을 찾을 수 없습니다."),

    // 훈련일지 관련
    TRAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "훈련일지를 찾을 수 없습니다."),
    WBS_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 시간대의 WBS 정보가 존재하지 않습니다."),
    WBS_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "해당 시간대는 이미 예약되어 있습니다."),

    // 설문 관련
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 설문을 찾을 수 없습니다."),

    // 배너 관련
    BANNER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배너를 찾을 수 없습니다."),
    INVALID_PRIORITY_RANGE(HttpStatus.BAD_REQUEST, "우선순위는 1~5 사이여야 합니다."),
    INVALID_BANNER_PERIOD(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다."),

    // SMS 문자 관련
    SMS_ALREADY_SENT(HttpStatus.BAD_REQUEST, "해당 수강생에게 이미 경고 메시지를 발송하였습니다."),

    // 서버 에러
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

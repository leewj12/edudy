package com.kosmo.backend.lecturepart.entity;

import com.kosmo.backend.attendance.LectureAttendanceEntity;
import com.kosmo.backend.consult.LectureConsultEntity;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.review.ReviewEntity;
import com.kosmo.backend.score.LectureScoreEntity;
import com.kosmo.backend.survey.LectureSurveyEntity;
import com.kosmo.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "lecture_part")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LecturePartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_part_id")
    private Long lecturePartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_status", nullable = false)
    @Builder.Default
    private LecturePartStatus lecturePartStatus = LecturePartStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_risk_level", nullable = false)
    @Builder.Default
    private LecturePartRiskLevel lecturePartRiskLevel = LecturePartRiskLevel.NORMAL;

    @Column(name = "lecture_part_emp", nullable = false)
    @Builder.Default
    private Boolean lecturePartEmp = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_danger", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartDanger = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month1", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth1 = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month2", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth2 = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month3", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth3 = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month4", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth4 = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month5", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth5 = LecturePartCondition.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_part_month6", nullable = false)
    @Builder.Default
    private LecturePartCondition lecturePartMonth6 = LecturePartCondition.NORMAL;

    @Column(name = "lecture_part_action_note")
    private String lecturePartActionNote;
//    REQUESTED       // 상담 요청됨
//    SENT_MESSAGE    // 문자 발송 완료
//    COMPLETED       // 상담 완료
//    EXCLUDED         // 예외 처리로 관리 대상에서 제외됨 (soft delete 성격)

    @Column(name = "lecture_part_late_cnt", nullable = false)
    @Builder.Default
    private Long lecturePartLateCnt = 0L;

    @Column(name = "lecture_part_leave_cnt", nullable = false)
    @Builder.Default
    private Long lecturePartLeaveCnt = 0L;

    @Column(name = "lecture_part_early_leave_cnt", nullable = false)
    @Builder.Default
    private Long lecturePartEarlyLeaveCnt = 0L;

    @Column(name = "lecture_part_absent", nullable = false)
    @Builder.Default
    private Long lecturePartAbsent = 0L;

    @Column(name = "lecture_part_all_att_rate", nullable = false)
    @Builder.Default
    private Long lecturePartAllAttRate = 100L;

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartInfoEntity> partInfos = new ArrayList<>();

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureAttendanceEntity> lectureAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureScoreEntity> lectureScores = new ArrayList<>();

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureConsultEntity> lectureConsults = new ArrayList<>();

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureSurveyEntity> lectureSurveys = new ArrayList<>();

//    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<InstrSurveyEntity> instrSurveys = new ArrayList<>();

    @OneToMany(mappedBy = "lecturePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();

    public void updateLecturePartEmp(Boolean emp) {
        this.lecturePartEmp = emp;
    }

    public void updateLecturePartStatus(LecturePartStatus status) {
        this.lecturePartStatus = status;
    }

    public void updateLecturePartRiskLevel(LecturePartRiskLevel level) {
        this.lecturePartRiskLevel = level;
    }

    public void updateLecturePartDanger(LecturePartCondition lecturePartDanger) {
        this.lecturePartDanger = lecturePartDanger;
    }

    public void updateLecturePartMonth1(LecturePartCondition lecturePartMonth1) {
        this.lecturePartMonth1 = lecturePartMonth1;
    }

    public void updateLecturePartMonth2(LecturePartCondition lecturePartMonth2) {
        this.lecturePartMonth2 = lecturePartMonth2;
    }

    public void updateLecturePartMonth3(LecturePartCondition lecturePartMonth3) {
        this.lecturePartMonth3 = lecturePartMonth3;
    }

    public void updateLecturePartMonth4(LecturePartCondition lecturePartMonth4) {
        this.lecturePartMonth4 = lecturePartMonth4;
    }

    public void updateLecturePartMonth5(LecturePartCondition lecturePartMonth5) {
        this.lecturePartMonth5 = lecturePartMonth5;
    }

    public void updateLecturePartMonth6(LecturePartCondition lecturePartMonth6) {
        this.lecturePartMonth6 = lecturePartMonth6;
    }

    public void updateLecturePartActionNote(String note) {
        this.lecturePartActionNote = note;
    }

    public void updateLateCnt(Long cnt) {
        this.lecturePartLateCnt = cnt;
    }

    public void updateLeaveCnt(Long cnt) {
        this.lecturePartLeaveCnt = cnt;
    }

    public void updateEarlyLeaveCnt(Long cnt) {
        this.lecturePartEarlyLeaveCnt = cnt;
    }

    public void updateAbsentCnt(Long cnt) {
        this.lecturePartAbsent = cnt;
    }

//    public void increaseLateCnt() {
//        this.lecturePartLateCnt += 1;
//    }

    public void increaseLateCnt() {
        this.lecturePartLateCnt = Optional.ofNullable(this.lecturePartLateCnt).orElse(0L) + 1;
    }

//    public void increaseLeaveCnt() {
//        this.lecturePartLeaveCnt = (this.lecturePartLeaveCnt == null ? 1 : this.lecturePartLeaveCnt + 1);
//        // ✅ “값이 무조건 있다고 믿지 말고, 혹시라도 빠져 있으면 어떻게 할지를 미리 코드로 막아두는 거”가 바로 방어적 프로그래밍이야
//    }

    public void increaseLeaveCnt() {
        this.lecturePartLeaveCnt = Optional.ofNullable(this.lecturePartLeaveCnt).orElse(0L) + 1;
    }

    public void increaseEarlyLeaveCnt() {
        this.lecturePartEarlyLeaveCnt = Optional.ofNullable(this.lecturePartEarlyLeaveCnt).orElse(0L) + 1;
    }

    public void decreaseLateCnt() {
        if (this.lecturePartLateCnt != null && this.lecturePartLateCnt > 0) {
            this.lecturePartLateCnt--;
        }
    }

    public void decreaseLeaveCnt() {
        if (this.lecturePartLeaveCnt != null && this.lecturePartLeaveCnt > 0) {
            this.lecturePartLeaveCnt--;
        }
    }

    public void decreaseEarlyLeaveCnt() {
        if (this.lecturePartEarlyLeaveCnt != null && this.lecturePartEarlyLeaveCnt > 0) {
            this.lecturePartEarlyLeaveCnt--;
        }
    }

    public void updateLecturePartAllAttRate(Long rate) {
        this.lecturePartAllAttRate = rate;
    }

    public int calculateRiskScore(int totalDays, int absentCount) {
        // 출석률 계산
        int attendedDays = totalDays - absentCount;
        //double attendanceRate = (double) attendedDays / totalDays * 100;
        Long attendanceRate = Math.round((double) attendedDays / totalDays * 100);
        this.updateLecturePartAllAttRate(attendanceRate); // ✅ 출석률 저장

        int score = 0;

        // 출석률 점수
        if (attendanceRate >= 95) score += 50;
        else if (attendanceRate >= 90) score += 40;
        else if (attendanceRate >= 87) score += 30;
        else if (attendanceRate >= 84) score += 20;
        else score += 10;

        // 결석 점수
        if (absentCount == 0) score += 20;
        else if (absentCount == 1) score += 15;
        else if (absentCount == 2) score += 10;
        else score += 5;

        // 지각/조퇴 통합 횟수 → 3번 = 1결석
        long combined = Optional.ofNullable(lecturePartLateCnt).orElse(0L) +
                Optional.ofNullable(lecturePartEarlyLeaveCnt).orElse(0L);
        long converted = combined / 3;

        if (converted <= 2) score += 30;
        else if (converted <= 5) score += 20;
        else if (converted <= 8) score += 10;
        else score += 5;

        return score;
    }

    public void updateRiskLevel(int totalDays, int absentCount) {
        int score = calculateRiskScore(totalDays, absentCount);
        this.lecturePartRiskLevel = getRiskLevelByScore(score);
    }

    private LecturePartRiskLevel getRiskLevelByScore(int score) {
        if (score >= 81) return LecturePartRiskLevel.NORMAL;
        else if (score >= 61) return LecturePartRiskLevel.LOW;
        else if (score >= 41) return LecturePartRiskLevel.MEDIUM;
        else return LecturePartRiskLevel.HIGH;
    }

    // Entity 내부에 메서드로만 제공
    public Long getCalculatedCurrentAttendanceRate() {
        long currentCnt = Optional.ofNullable(lecture).map(LectureEntity::getLectureCurrentCnt).orElse(0L);
        long absentCnt = Optional.ofNullable(lecturePartAbsent).orElse(0L);

        if (currentCnt == 0) return 100L;

        return Math.round(((double)(currentCnt - absentCnt) / currentCnt) * 100);
    }
}
package com.kosmo.backend.attendance;

import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_att")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureAttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_att_id")
    private Long lectureAttId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "att_date", nullable = false)
    private LocalDate attDate;

    @Column(name = "att_entry")
    private LocalDateTime attEntry;

    @Column(name = "att_exit")
    private LocalDateTime attExit;

    @Column(name = "att_leave_start")
    private LocalDateTime attLeaveStart;

    @Column(name = "att_leave_end")
    private LocalDateTime attLeaveEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "att_status", nullable = false)
    private AttStatus attStatus; // 예: 결석, 입실, 외출, 퇴실

    @Column(name = "att_late", nullable = false)
    private boolean attLate = false;

    @Column(name = "att_leave", nullable = false)
    private boolean attLeave = false;

    @Column(name = "att_early_leave", nullable = false)
    private boolean attEarlyLeave = false;

    // ✅ 출석 인정 사유 코드
    @Enumerated(EnumType.STRING)
    @Column(name = "att_reason_code")
    private AttReasonCode attReasonCode; // 예 : 휴가, 병원, 병역, 면접/시험, 기타

    // ✅ 출석 인정 사유 상세
    @Column(name = "att_reason_detail")
    private String attReasonDetail;

    // ==================== 업데이트 메서드 ====================

    public void updateOuting(LocalDateTime leaveStart) {
        this.attLeaveStart = leaveStart;
        this.attLeave = true;
        this.attStatus = AttStatus.OUTING;
    }

    public void updateOutingEnd(LocalDateTime leaveEndTime) {
        this.attLeaveEnd = leaveEndTime;
        this.attStatus = AttStatus.ENTRY;
    }

    public void updateExit(LocalDateTime exitTime, boolean isEarlyLeave) {
        this.attLeaveEnd = exitTime;
        this.attEarlyLeave = isEarlyLeave;
        this.attStatus = AttStatus.EXIT;
    }

    public void updateAttStatus(AttStatus attStatus) {
        this.attStatus = attStatus;
    }

    public void updateAttFalse() {
        // 누락된 시간이 너무 많으므로 개별 지각/외출/조퇴는 무의미 → 모두 false로 초기화
        this.attLate = false;
        this.attLeave = false;
        this.attEarlyLeave = false;
    }

    public void updateRecognizedStatus(AttReasonCode reasonCode, String reasonDetail) {
        this.attStatus = AttStatus.RECOGNIZED;
        this.attReasonCode = reasonCode;
        this.attReasonDetail = reasonDetail;
        updateAttFalse(); // 지각, 외출, 조퇴 플래그 초기화
    }
}
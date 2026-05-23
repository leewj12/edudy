package com.kosmo.backend.lecture.ask;

import com.kosmo.backend.lecture.entity.LectureEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_ask")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureAskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_ask_id")
    private Long lectureAskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private LectureEntity lecture;

    @Column(name = "ask_name", nullable = false)
    private String askName;

    @Column(name = "ask_phone", nullable = false)
    private String askPhone;

    @Column(name = "ask_card", nullable = false)
    private Boolean askCard;

    @Column(name = "ask_memo")
    private String askMemo;

    @Enumerated(EnumType.STRING) // Enum의 이름(String)을 DB에 저장
    @Column(name = "ask_status", nullable = false)
    private AskStatus askStatus;

    @CreatedDate
    @Column(name = "ask_created_at", nullable = false, updatable = false)
    private LocalDateTime askCreatedAt;

    @LastModifiedDate
    @Column(name = "ask_updated_at")
    private LocalDateTime askUpdatedAt;

    public void updateStatus(AskStatus status) {
        this.askStatus = status;
    }
}
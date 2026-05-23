package com.kosmo.backend.consult;

import com.kosmo.backend.consult.dto.LectureConsultUpdateRequest;
import com.kosmo.backend.lecturepart.entity.LecturePartEntity;
import com.kosmo.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_consult")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class LectureConsultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_consult_id")
    private Long lectureConsultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // 🔁 관리자 ID
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "consult_title", nullable = false)
    private String consultTitle;

    @Column(name = "consult_date", nullable = false)
    private LocalDate consultDate;

    @Column(name = "consult_content", nullable = false)
    private String consultContent;

    @Column(name = "consult_special")
    private String consultSpecial;

    @Enumerated(EnumType.STRING)
    @Column(name = "consult_type", nullable = false)
    private ConsultType consultType;

    @Enumerated(EnumType.STRING)
    @Column(name = "consult_keyword", nullable = false)
    private ConsultKeyword consultKeyword;

    @CreatedDate
    @Column(name = "consult_created_at", nullable = false, updatable = false)
    private LocalDateTime consultCreatedAt;

    @LastModifiedDate
    @Column(name = "consult_updated_at")
    private LocalDateTime consultUpdatedAt;

    public void updateConsult(LectureConsultUpdateRequest request) {
        this.consultTitle = request.getConsultTitle();
        this.consultDate = request.getConsultDate();
        this.consultContent = request.getConsultContent();
        this.consultSpecial = request.getConsultSpecial();
        this.consultType = request.getConsultType();
        this.consultKeyword = request.getConsultKeyword();
    }
}
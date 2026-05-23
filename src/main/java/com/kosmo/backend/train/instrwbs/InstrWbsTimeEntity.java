package com.kosmo.backend.train.instrwbs;

import com.kosmo.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "instr_wbs_time")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class InstrWbsTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instr_wbs_time_id")
    private Long instrWbsTimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity instructor; // 직접 강사(UserEntity)와 연관

    @Column(name = "wbs_date", nullable = false)
    private LocalDate wbsDate;

    @Column(name = "wbs_time", nullable = false)
    private Long wbsTime; // 1~8

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private boolean isAvailable = false;

    @CreatedDate
    @Column(name = "wbs_created_at", nullable = false, updatable = false)
    private LocalDateTime wbsCreatedAt;

    @LastModifiedDate
    @Column(name = "wbs_updated_at")
    private LocalDateTime wbsUpdatedAt;

    public void updateAvailability(boolean available) {
        this.isAvailable = available;
    }
}
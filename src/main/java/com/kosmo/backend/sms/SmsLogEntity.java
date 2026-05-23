package com.kosmo.backend.sms;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SmsLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    private Long id;

    @Column(name = "sms_to_phone", nullable = false)
    private String toPhone;

    @Column(name = "sms_from_phone", nullable = false)
    private String fromPhone;

    @Column(name = "sms_text", nullable = false)
    private String text;

    @Column(name = "sms_success", nullable = false)
    private Boolean success;

    @Column(name = "sms_response_code", nullable = false)
    private String responseCode;

    @Column(name = "sms_response_message", nullable = false)
    private String responseMessage;

    @Column(name = "sms_created_at", nullable = false)
    private LocalDateTime sentAt;
}
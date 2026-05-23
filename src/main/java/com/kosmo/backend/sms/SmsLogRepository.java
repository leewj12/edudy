package com.kosmo.backend.sms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SmsLogRepository extends JpaRepository<SmsLogEntity, Long> {
    boolean existsByToPhoneAndTextAndSentAtAfter(String toPhone, String text, LocalDateTime sentAt);
}

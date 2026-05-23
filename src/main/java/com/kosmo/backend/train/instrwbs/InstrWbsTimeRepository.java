package com.kosmo.backend.train.instrwbs;

import com.kosmo.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface InstrWbsTimeRepository extends JpaRepository<InstrWbsTimeEntity, Long> {
    Optional<InstrWbsTimeEntity> findByInstructorAndWbsDateAndWbsTime(
            UserEntity instructor,
            LocalDate wbsDate,
            Long wbsTime
    );
}

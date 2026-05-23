package com.kosmo.backend.lecturepart.license;

import com.kosmo.backend.lecturepart.entity.PartInfoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "license")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LicenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    private Long licenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_info_id", nullable = false)
    private PartInfoEntity partInfo;

    @Column(name = "license_name", nullable = false)
    private String licenseName;

    @Column(name = "license_date", nullable = false)
    private LocalDate licenseDate;
}
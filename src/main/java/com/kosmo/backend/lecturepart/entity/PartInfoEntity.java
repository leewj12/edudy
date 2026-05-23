package com.kosmo.backend.lecturepart.entity;

import com.kosmo.backend.lecturepart.career.CareerEntity;
import com.kosmo.backend.lecturepart.hope.HopeEntity;
import com.kosmo.backend.lecturepart.license.LicenseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "part_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class PartInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_info_id")
    private Long partInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_part_id", nullable = false)
    private LecturePartEntity lecturePart;

    @Column(name = "info_special")
    private String infoSpecial;

    @OneToMany(mappedBy = "partInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LicenseEntity> licenses = new ArrayList<>();

    @OneToMany(mappedBy = "partInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HopeEntity> hopes = new ArrayList<>();

    @OneToMany(mappedBy = "partInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerEntity> careers = new ArrayList<>();

}
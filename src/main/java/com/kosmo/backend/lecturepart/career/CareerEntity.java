package com.kosmo.backend.lecturepart.career;

import com.kosmo.backend.lecturepart.entity.PartInfoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "career")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class CareerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long careerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_info_id", nullable = false)
    private PartInfoEntity partInfo;

    @Column(name = "career_name", nullable = false)
    private String careerName;

    @Column(name = "career_start", nullable = false)
    private LocalDate careerStart;

    @Column(name = "career_end", nullable = false)
    private LocalDate careerEnd;
}

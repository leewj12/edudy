package com.kosmo.backend.lecturepart.hope;

import com.kosmo.backend.lecturepart.entity.PartInfoEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hope")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class HopeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hope_id")
    private Long hopeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_info_id", nullable = false)
    private PartInfoEntity partInfo;

    @Column(name = "hope1", nullable = false)
    private String hope1;

    @Column(name = "hope2")
    private String hope2;

    @Column(name = "hope3")
    private String hope3;
}

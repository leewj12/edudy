package com.kosmo.backend.lecture.category;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecture_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class LectureCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_category_id")
    private Long lectureCategoryId;

    @Column(name = "lecture_category_name", nullable = false, unique = true)
    private String lectureCategoryName;

//    @OneToMany(mappedBy = "lectureCategory", cascade = CascadeType.ALL)
//    LectureCategory 가 삭제되면 →
//    그에 속한 모든 강의(LectureEntity)가 같이 삭제
    @OneToMany(mappedBy = "lectureCategory") // cascade ❌ 제거, orphanRemoval ❌ 제거
    private List<LectureEntity> lectures = new ArrayList<>();

    public void updateName(String name) {
        this.lectureCategoryName = name;
    }

    // 하위 강의를 포함하고 있을 경우 삭제를 막는 로직
    @PreRemove
    private void preRemove() {
        if (!lectures.isEmpty()) {
            throw new CustomAuthException(ErrorCode.CATEGORY_HAS_LECTURES);
        }
    }
}
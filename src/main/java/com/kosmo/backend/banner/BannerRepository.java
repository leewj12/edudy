package com.kosmo.backend.banner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {
    // 활성화된 배너를 우선순위 순으로 정렬하여 조회
    List<BannerEntity> findAllByBannerStatusTrueOrderByBannerPriorityAsc();
    Optional<BannerEntity> findFirstByLecture_LectureId(Long lectureId);
}

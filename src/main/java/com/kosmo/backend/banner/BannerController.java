package com.kosmo.backend.banner;


import com.kosmo.backend.banner.dto.BannerResponse;
import com.kosmo.backend.banner.dto.BannerUpdatePriorityStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("/admin/banner")
    public ResponseEntity<?> createBanner(
            @RequestParam("lectureId") Long lectureId,
            @RequestParam("bannerPriority") Long bannerPriority,
            @RequestParam(value = "bannerImageFile", required = false) MultipartFile bannerImageFile
    ) {
        bannerService.createBanner(lectureId, bannerPriority, bannerImageFile);
        return ResponseEntity.ok("배너가 등록되었습니다.");
    }

    // 전체 배너 조회
    @GetMapping("/admin/banner")
    public ResponseEntity<List<BannerResponse>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    // 개별 배너 상세 조회
    @GetMapping("/admin/banner/{bannerId}")
    public ResponseEntity<BannerResponse> getBannerById(@PathVariable Long bannerId) {
        return ResponseEntity.ok(bannerService.getBannerById(bannerId));
    }

    // 활성 배너를 우선순위 순으로 조회
    @GetMapping("/admin/banner/active")
    public ResponseEntity<List<BannerResponse>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBannersSortedByPriority());
    }

    // 활성 배너를 우선순위 순으로 조회(user)
    @GetMapping("/user/banner/active")
    public ResponseEntity<List<BannerResponse>> getActiveBannersUser() {
        return ResponseEntity.ok(bannerService.getActiveBannersSortedByPriority());
    }

    // 활성 배너를 우선순위 순으로 조회(user)
    @GetMapping("/guest/banner/active")
    public ResponseEntity<List<BannerResponse>> getActiveBannersGuest() {
        return ResponseEntity.ok(bannerService.getActiveBannersSortedByPriority());
    }

    @PutMapping("/admin/banner/{bannerId}")
    public ResponseEntity<?> updateBanner(
            @PathVariable Long bannerId,
            @RequestParam("lectureId") Long lectureId,
            @RequestParam("bannerPriority") Long bannerPriority,
            @RequestParam(value = "bannerImageFile", required = false) MultipartFile bannerImageFile
    ) {
        bannerService.updateBanner(bannerId, lectureId, bannerPriority, bannerImageFile);
        return ResponseEntity.ok("배너가 수정되었습니다.");
    }

    @PatchMapping("/admin/banner/{bannerId}/settings")
    public ResponseEntity<?> updateBannerSettings(
            @PathVariable Long bannerId,
            @RequestBody BannerUpdatePriorityStatusRequest request
    ) {
        bannerService.updateBannerSettings(bannerId, request);
        return ResponseEntity.ok("배너 우선순위, 노출상태, 기간이 수정되었습니다.");
    }

    @DeleteMapping("/admin/banner/{bannerId}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        return ResponseEntity.ok("배너가 삭제되었습니다.");
    }

}
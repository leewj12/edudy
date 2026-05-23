package com.kosmo.backend.banner;


import com.kosmo.backend.banner.dto.BannerResponse;
import com.kosmo.backend.banner.dto.BannerUpdatePriorityStatusRequest;
import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void createBanner(Long lectureId, Long bannerPriority, MultipartFile bannerImageFile) {
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        // 기본 이미지 설정
        String bannerImageFilename = "bannerDefault.jpg";

        if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
            bannerImageFilename = storeFile(bannerImageFile, "banner");
        }

        BannerEntity banner = BannerEntity.builder()
                .bannerImage(bannerImageFilename)
                .bannerPriority(bannerPriority)
                .bannerStatus(true)
                .lecture(lecture)
                .build();

        bannerRepository.save(banner);
    }


    // 배너 전체 조회
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 배너 상세 조회
    public BannerResponse getBannerById(Long bannerId) {
        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));
        return toDto(banner);
    }

    private BannerResponse toDto(BannerEntity entity) {
        return BannerResponse.builder()
                .bannerId(entity.getBannerId())
                .bannerImage(entity.getBannerImage())
                .bannerContent(entity.getBannerContent())
                .bannerPriority(entity.getBannerPriority())
                .bannerStatus(entity.getBannerStatus())
                .bannerStart(entity.getBannerStart())
                .bannerEnd(entity.getBannerEnd())
                .lectureId(entity.getLecture().getLectureId())
                .lectureTitle(entity.getLecture().getLectureTitle())
                .build();
    }

    public List<BannerResponse> getActiveBannersSortedByPriority() {
        return bannerRepository.findAllByBannerStatusTrueOrderByBannerPriorityAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void updateBanner(Long bannerId, Long lectureId, Long bannerPriority, MultipartFile bannerImageFile) {
//        BannerEntity banner = bannerRepository.findById(bannerId)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));
//
//        LectureEntity lecture = lectureRepository.findById(lectureId)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));
//
//        // ✅ 업로드 경로 (루트 기준 상대 경로로 설정)
//        String basePath = Paths.get("").toAbsolutePath() + "/upload/";
//
//        // 새 이미지가 들어온 경우에만 저장
//        if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
//            String oldImage = banner.getBannerImage();
//            if (oldImage != null && !oldImage.equals("bannerDefault.jpg")) {
//                Path oldPath = Paths.get(basePath + "banner/" + oldImage);
//                try {
//                    Files.deleteIfExists(oldPath);
//                } catch (IOException e) {
//                    throw new RuntimeException("기존 배너 이미지 삭제 실패", e);
//                }
//            }
//
//            String newFilename = storeFile(bannerImageFile, "banner");
//            banner.updateImage(newFilename); // 배너 엔티티에 이미지 setter 필요
////            newImageFilename = storeFile(bannerImageFile, "banner");
//        }
//
////        banner.update(
////                request.getBannerContent(),
////                request.getBannerPriority(),
////                request.getBannerStatus(),
////                request.getBannerStart(),
////                request.getBannerEnd(),
////                lecture
////        );
//        // 기타 정보 업데이트
//        banner.updatePriorityAndLecture(bannerPriority, lecture);
//    }
@Transactional
public void updateBanner(Long bannerId, Long lectureId, Long bannerPriority, MultipartFile bannerImageFile) {
    BannerEntity banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));

    LectureEntity lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

    // ✅ user.dir 기준으로 설정 (Docker: /app/upload/, 로컬: ./upload/)
    String basePath = System.getProperty("user.dir") + "/upload/banner/";

    if (bannerImageFile != null && !bannerImageFile.isEmpty()) {
        String oldImage = banner.getBannerImage();
        if (oldImage != null && !oldImage.equals("bannerDefault.jpg")) {
            Path oldPath = Paths.get(basePath, oldImage);
            try {
                Files.deleteIfExists(oldPath);
            } catch (IOException e) {
                throw new RuntimeException("기존 배너 이미지 삭제 실패", e);
            }
        }

        String newFilename = storeFile(bannerImageFile, "banner");
        banner.updateImage(newFilename);
    }

    banner.updatePriorityAndLecture(bannerPriority, lecture);
}



    @Transactional
    public void updateBannerSettings(Long bannerId, BannerUpdatePriorityStatusRequest request) {
//        if (request.getPriority() < 1 || request.getPriority() > 5) {
//            throw new CustomAuthException(ErrorCode.INVALID_PRIORITY_RANGE);
//        }

        if (request.getBannerStart() != null && request.getBannerEnd() != null &&
                request.getBannerStart().isAfter(request.getBannerEnd())) {
            throw new CustomAuthException(ErrorCode.INVALID_BANNER_PERIOD);
        }

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));

        banner.updatePriorityStatusAndPeriod(
                request.getPriority(),
                request.getStatus(),
                request.getBannerStart(),
                request.getBannerEnd()
        );
    }

//    @Transactional
//    public void deleteBanner(Long bannerId) {
//        BannerEntity banner = bannerRepository.findById(bannerId)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));
//
//        String basePath = Paths.get("").toAbsolutePath() + "/upload/";
//
//        // 배너 이미지 삭제
//        String bannerImage = banner.getBannerImage();
//        if (bannerImage != null && !bannerImage.equals("bannerDefault.jpg")) {
//            Path imagePath = Paths.get(basePath + "banner/" + bannerImage);
//            try {
//                Files.deleteIfExists(imagePath);
//            } catch (IOException e) {
//                throw new RuntimeException("배너 이미지 파일 삭제 실패", e);
//            }
//        }
//
//        // 배너 DB 삭제
//        bannerRepository.delete(banner);
//    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.BANNER_NOT_FOUND));

        // ✅ user.dir 기준으로 배너 이미지 삭제
        String bannerImage = banner.getBannerImage();
        if (bannerImage != null && !bannerImage.equals("bannerDefault.jpg")) {
            String uploadBasePath = System.getProperty("user.dir") + "/upload/banner/";
            Path imagePath = Paths.get(uploadBasePath, bannerImage);
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("배너 이미지 파일 삭제 실패", e);
            }
        }

        // DB에서 배너 엔티티 삭제
        bannerRepository.delete(banner);
    }

//    private String storeFile(MultipartFile file, String folder) {
//        try {
//            String uuid = UUID.randomUUID().toString();
//            String ext = Objects.requireNonNull(file.getOriginalFilename())
//                    .substring(file.getOriginalFilename().lastIndexOf("."));
//            String filename = uuid + ext;
//
////            String uploadBasePath = Paths.get("").toAbsolutePath().toString() + "/upload/" + folder;
//            // ✅ webapp 내부의 /upload/banner/ 등 경로로 저장
//            String uploadBasePath = servletContext.getRealPath("/upload/" + folder);
//            Path savePath = Paths.get(uploadBasePath, filename);
//
//            Files.createDirectories(savePath.getParent());
//
//            file.transferTo(savePath.toFile());
//
//            return filename;
//        } catch (IOException e) {
//            throw new RuntimeException("파일 업로드 실패", e);
//        }
//    }


    private String storeFile(MultipartFile file, String folder) {
        try {
            String uuid = UUID.randomUUID().toString();
            String ext = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = uuid + ext;

            // ✅ user.dir 기준으로 저장 (Docker: /app/upload/, 로컬: ./upload/)
            String uploadBasePath = System.getProperty("user.dir") + "/upload/" + folder;
            Path savePath = Paths.get(uploadBasePath, filename);

            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath.toFile());

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }



}
package com.kosmo.backend.lecture.service;

import com.kosmo.backend.attendance.holiday.HolidayService;
import com.kosmo.backend.banner.BannerEntity;
import com.kosmo.backend.banner.BannerRepository;
import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.lecture.category.LectureCategoryEntity;
import com.kosmo.backend.lecture.category.LectureCategoryRepository;
import com.kosmo.backend.lecture.dto.*;
import com.kosmo.backend.lecture.entity.LectureEntity;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.lecture.subject.LectureSubjectEntity;
import com.kosmo.backend.lecture.subject.LectureSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LectureService {

    private final HolidayService holidayService;
    private final LectureRepository lectureRepository;
    private final LectureSubjectRepository lectureSubjectRepository;
    private final LectureCategoryRepository lectureCategoryRepository;
    private final BannerRepository bannerRepository;

    @Transactional
    public void createLecture(LectureCreateRequest request, MultipartFile thumbnailFile, MultipartFile contentImageFile) {
        // 1. 파일 처리 (생략 부분 동일)
        String thumbnailFilename = "lectureNoImage.jpg"; // 기본값
        String contentImageFilename = "lectureContentDefault.jpg";

        // ✅ 썸네일 파일 저장
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailFilename = storeFile(thumbnailFile, "lecture/thumbnail");
        }

        // ✅ 강의 내용 이미지 저장
        if (contentImageFile != null && !contentImageFile.isEmpty()) {
            contentImageFilename = storeFile(contentImageFile, "lecture/content");
        }

        // 2. 카테고리 조회 (없으면 예외)
        LectureCategoryEntity category = lectureCategoryRepository.findById(request.getLectureCategoryId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));

        // ✅ 전체 수업일 계산 (주말 제외)
        long totalDays = calculateLectureAllDays(request.getLectureStart(), request.getLectureEnd());

        // 3. 엔티티 빌드
        LectureEntity lecture = LectureEntity.builder()
                .lectureTitle(request.getLectureTitle())
                .lectureShortTitle(request.getLectureShortTitle())
                .lectureDescription(request.getLectureDescription())
                .lecturePrice(request.getLecturePrice())
                .lectureCapacity(request.getLectureCapacity())
                .lecturePostcode(request.getLecturePostcode())
                .lectureAddress(request.getLectureAddress())
                .lectureAddressDetail(request.getLectureAddressDetail())
                .lectureStart(request.getLectureStart())
                .lectureEnd(request.getLectureEnd())
                .lectureStartTime(request.getLectureStartTime())
                .lectureEndTime(request.getLectureEndTime())
                .lectureThumbnail(thumbnailFilename)
                .lectureContentImage(contentImageFilename) // ✅ 추가
                .lectureLayoutStart(request.getLectureLayoutStart())
                .lectureLayoutEnd(request.getLectureLayoutEnd())
                .lectureAllDate(totalDays) // ✅ 여기에 계산된 값 넣기
                // lecturePriority, lectureStatus는 Entity의 기본값으로 처리
                .lectureCategory(category) // ✅ 카테고리 설정
                .build();

        lectureRepository.save(lecture);
    }

    @Transactional
    public void createLectureWithSubjects(LectureWithSubjectsCreateRequest request, MultipartFile thumbnailFile, MultipartFile contentImageFile) {
        LectureCreateRequest lectureReq = request.getLecture();

        // 1. 파일 저장 로직 동일
        String thumbnailFilename = "lectureNoImage.jpg";
        String contentImageFilename = "lectureContentDefault.jpg";

        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbnailFilename = storeFile(thumbnailFile, "lecture/thumbnail");
        }
        if (contentImageFile != null && !contentImageFile.isEmpty()) {
            contentImageFilename = storeFile(contentImageFile, "lecture/content");
        }

        // 2. 카테고리 조회
        LectureCategoryEntity category = lectureCategoryRepository.findById(lectureReq.getLectureCategoryId())
                .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));

        // 3. 수업일 계산
        long totalDays = calculateLectureAllDays(lectureReq.getLectureStart(), lectureReq.getLectureEnd());

        // 4. 강의 저장
        LectureEntity lecture = LectureEntity.builder()
                .lectureTitle(lectureReq.getLectureTitle())
                .lectureShortTitle(lectureReq.getLectureShortTitle())
                .lectureDescription(lectureReq.getLectureDescription())
                .lecturePrice(lectureReq.getLecturePrice())
                .lectureCapacity(lectureReq.getLectureCapacity())
                .lecturePostcode(lectureReq.getLecturePostcode())
                .lectureAddress(lectureReq.getLectureAddress())
                .lectureAddressDetail(lectureReq.getLectureAddressDetail())
                .lectureStart(lectureReq.getLectureStart())
                .lectureEnd(lectureReq.getLectureEnd())
                .lectureStartTime(lectureReq.getLectureStartTime())
                .lectureEndTime(lectureReq.getLectureEndTime())
                .lectureThumbnail(thumbnailFilename)
                .lectureContentImage(contentImageFilename)
                .lectureLayoutStart(lectureReq.getLectureLayoutStart())
                .lectureLayoutEnd(lectureReq.getLectureLayoutEnd())
                .lectureAllDate(totalDays)
                .lectureCategory(category)
                .build();

        lectureRepository.save(lecture);

        // 5. 과목 저장
        if (request.getSubjectTitles() != null && !request.getSubjectTitles().isEmpty()) {
            List<LectureSubjectEntity> subjects = request.getSubjectTitles().stream()
                    .map(title -> LectureSubjectEntity.builder()
                            .lecture(lecture)
                            .subjectTitle(title)
                            .build())
                    .toList();

            lectureSubjectRepository.saveAll(subjects);
        }
    }


    public List<LectureResponse> getAllLectures() {
        return lectureRepository.findAll()
                .stream()
//                .map(LectureResponse::fromEntity)
                .map(this::toLectureResponseWithBanner)
                .toList();
    }

    public List<LectureResponse> getAllLecturesByStartDesc() {
        List<LectureEntity> lectures = lectureRepository.findAllByOrderByLectureStartDesc();
        return lectures.stream()
                .map(this::toLectureResponseWithBanner)
                .collect(Collectors.toList());
    }

    public List<LectureResponse> getAllLecturesByEnrolledDesc() {
        List<LectureEntity> lectures = lectureRepository.findAllByOrderByLectureEnrolledDesc();
        return lectures.stream()
                .map(this::toLectureResponseWithBanner)
                .toList();
    }

    public List<LectureResponse> findLatestLecturesByCategory(Long categoryId) {
        List<LectureEntity> lectures = lectureRepository.findAllByLectureCategory_LectureCategoryIdOrderByLectureStartDesc(categoryId);
        return lectures.stream()
                .map(this::toLectureResponseWithBanner)
                .collect(Collectors.toList());
    }

    public List<LectureResponse> findPopularLecturesByCategory(Long categoryId) {
        List<LectureEntity> lectures = lectureRepository.findAllByLectureCategory_LectureCategoryIdOrderByLectureEnrolledDesc(categoryId);
        return lectures.stream()
                .map(this::toLectureResponseWithBanner)
                .collect(Collectors.toList());
    }

    public List<LectureResponse> searchLecturesByTitle(String keyword) {
        List<LectureEntity> lectures = lectureRepository.findByLectureTitleContainingIgnoreCaseOrderByLectureStartDesc(keyword);
        return lectures.stream()
                .map(this::toLectureResponseWithBanner)
                .toList();
    }

    public List<LecturePriorityResponse> getTopPriorityLectures() {
        List<LectureEntity> lectures = lectureRepository.findByLecturePriorityBetweenOrderByLecturePriorityAsc(1L, 5L);
        return lectures.stream()
                .map(LecturePriorityResponse::fromEntity)
                .toList();
    }

    public LectureResponse getLectureById(Long id) {
        LectureEntity lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));
        return toLectureResponseWithBanner(lecture);
    }

    public List<LectureResponse> getLecturesByCategory(Long categoryId) {
        return lectureRepository.findByLectureCategory_LectureCategoryId(categoryId)
                .stream()
                .map(this::toLectureResponseWithBanner)
                .toList();
    }

    private LectureResponse toLectureResponseWithBanner(LectureEntity lecture) {
        BannerEntity banner = bannerRepository.findFirstByLecture_LectureId(lecture.getLectureId()).orElse(null);
        return LectureResponse.fromEntityWithBanner(lecture, banner);
    }
    // LectureService.java
//    @Transactional
//    public void deleteLecture(Long id) {
//        LectureEntity lecture = lectureRepository.findById(id)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));
//
//        String basePath = Paths.get("").toAbsolutePath() + "/upload/";
//
//        // 썸네일 이미지 삭제
//        String thumbnail = lecture.getLectureThumbnail();
//        if (thumbnail != null && !thumbnail.equals("lectureNoImage.jpg")) {
//            Path thumbnailPath = Paths.get(basePath + "lecture/thumbnail/" + thumbnail);
//            try {
//                Files.deleteIfExists(thumbnailPath);
//            } catch (IOException e) {
//                throw new RuntimeException("썸네일 파일 삭제 실패", e);
//            }
//        }
//
//        // 내용 이미지 삭제
//        String contentImage = lecture.getLectureContentImage();
//        if (contentImage != null && !contentImage.equals("lectureContentDefault.jpg")) {
//            Path contentPath = Paths.get(basePath + "lecture/content/" + contentImage);
//            try {
//                Files.deleteIfExists(contentPath);
//            } catch (IOException e) {
//                throw new RuntimeException("강의 내용 이미지 삭제 실패", e);
//            }
//        }
//
//        // 강의 자체 삭제
//        lectureRepository.delete(lecture);
//    }
    @Transactional
    public void deleteLecture(Long id) {
        LectureEntity lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        String thumbnail = lecture.getLectureThumbnail();
        if (thumbnail != null && !thumbnail.equals("lectureNoImage.jpg")) {
            Path thumbnailPath = Paths.get(System.getProperty("user.dir") + "/upload/lecture/thumbnail/", thumbnail);
            try {
                Files.deleteIfExists(thumbnailPath);
            } catch (IOException e) {
                throw new RuntimeException("썸네일 파일 삭제 실패", e);
            }
        }

        String contentImage = lecture.getLectureContentImage();
        if (contentImage != null && !contentImage.equals("lectureContentDefault.jpg")) {
            Path contentPath = Paths.get(System.getProperty("user.dir") + "/upload/lecture/content/", contentImage);
            try {
                Files.deleteIfExists(contentPath);
            } catch (IOException e) {
                throw new RuntimeException("강의 내용 이미지 삭제 실패", e);
            }
        }

        lectureRepository.delete(lecture);
    }


//    @Transactional
//    public void updateLecture(Long id, LectureUpdateRequest request,
//                              MultipartFile thumbnailFile,
//                              MultipartFile contentImageFile) {
//        LectureEntity lecture = lectureRepository.findById(id)
//                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));
//
//        // ✅ 업로드 경로 (루트 기준 상대 경로로 설정)
//        String basePath = Paths.get("").toAbsolutePath() + "/upload/";
//
//        // ✅ 썸네일 파일 업데이트
//        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
//            String oldThumbnail = lecture.getLectureThumbnail();
//            if (oldThumbnail != null && !oldThumbnail.equals("lectureNoImage.jpg")) {
//                Path oldPath = Paths.get(basePath + "lecture/thumbnail/" + oldThumbnail);
//                try {
//                    Files.deleteIfExists(oldPath);
//                } catch (IOException e) {
//                    throw new RuntimeException("기존 썸네일 삭제 실패", e);
//                }
//            }
//
//            String newThumbnailName = storeFile(thumbnailFile, "lecture/thumbnail");
//            lecture.updateLectureThumbnail(newThumbnailName);
//        }
//
//        // ✅ 강의 내용 이미지 파일 업데이트
//        if (contentImageFile != null && !contentImageFile.isEmpty()) {
//            String oldContentImage = lecture.getLectureContentImage();
//            if (oldContentImage != null && !oldContentImage.equals("lectureContentDefault.jpg")) {
//                Path oldPath = Paths.get(basePath + "lecture/content/" + oldContentImage);
//                try {
//                    Files.deleteIfExists(oldPath);
//                } catch (IOException e) {
//                    throw new RuntimeException("기존 내용 이미지 삭제 실패", e);
//                }
//            }
//
//            String newContentImageName = storeFile(contentImageFile, "lecture/content");
//            lecture.updateLectureContentImage(newContentImageName); // 이 메서드 LectureEntity에 있어야 함
//        }
//
//        // ✅ 카테고리 변경 처리
//        if (request.getLectureCategoryId() != null) {
//            LectureCategoryEntity category = lectureCategoryRepository.findById(request.getLectureCategoryId())
//                    .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));
//            lecture.changeLectureCategory(category);
//        }
//
//        // ✅ 기본 강의 정보 수정
//        lecture.updateLectureInfo(request); // 변경 감지 방식!
//
//        // ✅ 총 수업일수 업데이트
//        long totalDays = calculateLectureAllDays(request.getLectureStart(), request.getLectureEnd());
//        lecture.changeLectureAllDate(totalDays);
//    }

    @Transactional
    public void updateLecture(Long id, LectureUpdateRequest request,
                              MultipartFile thumbnailFile,
                              MultipartFile contentImageFile) {
        LectureEntity lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        // ✅ 썸네일 파일 업데이트
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String oldThumbnail = lecture.getLectureThumbnail();
            if (oldThumbnail != null && !oldThumbnail.equals("lectureNoImage.jpg")) {
                Path oldPath = Paths.get(System.getProperty("user.dir") + "/upload/lecture/thumbnail/", oldThumbnail);
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    throw new RuntimeException("기존 썸네일 삭제 실패", e);
                }
            }

            String newThumbnailName = storeFile(thumbnailFile, "lecture/thumbnail");
            lecture.updateLectureThumbnail(newThumbnailName);
        }

        // ✅ 강의 내용 이미지 파일 업데이트
        if (contentImageFile != null && !contentImageFile.isEmpty()) {
            String oldContentImage = lecture.getLectureContentImage();
            if (oldContentImage != null && !oldContentImage.equals("lectureContentDefault.jpg")) {
                Path oldPath = Paths.get(System.getProperty("user.dir") + "/upload/lecture/content/", oldContentImage);
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    throw new RuntimeException("기존 내용 이미지 삭제 실패", e);
                }
            }

            String newContentImageName = storeFile(contentImageFile, "lecture/content");
            lecture.updateLectureContentImage(newContentImageName);
        }

        // ✅ 카테고리 변경 처리
        if (request.getLectureCategoryId() != null) {
            LectureCategoryEntity category = lectureCategoryRepository.findById(request.getLectureCategoryId())
                    .orElseThrow(() -> new CustomAuthException(ErrorCode.CATEGORY_NOT_FOUND));
            lecture.changeLectureCategory(category);
        }

        // ✅ 기본 강의 정보 수정
        lecture.updateLectureInfo(request);

//        if (request.getSubjectTitles() != null) {
//            List<String> newTitles = request.getSubjectTitles();
//            List<LectureSubjectEntity> currentSubjects = lecture.getLectureSubjects();
//
//            // 1. 새로 들어온 제목 집합
//            Set<String> newTitleSet = new HashSet<>(newTitles);
//
//            // 2. 현재 과목 중에서 사라진 과목 제거
//            List<LectureSubjectEntity> toRemove = currentSubjects.stream()
//                    .filter(subject -> !newTitleSet.contains(subject.getSubjectTitle()))
//                    .toList();
//            lectureSubjectRepository.deleteAll(toRemove);
//
//            // 3. 현재 과목 제목 Set
//            Set<String> currentTitleSet = currentSubjects.stream()
//                    .map(LectureSubjectEntity::getSubjectTitle)
//                    .collect(Collectors.toSet());
//
//            // 4. 새로 들어온 과목 중에서 기존에 없는 과목은 추가
//            List<LectureSubjectEntity> toAdd = newTitles.stream()
//                    .filter(title -> !currentTitleSet.contains(title))
//                    .map(title -> LectureSubjectEntity.builder()
//                            .lecture(lecture)
//                            .subjectTitle(title)
//                            .build())
//                    .toList();
//
//            lectureSubjectRepository.saveAll(toAdd);
//        }
        if (request.getSubjects() != null) {
            List<LectureSubjectUpdateDto> newSubjects = request.getSubjects(); // subjectId + subjectTitle 포함된 DTO
            List<LectureSubjectEntity> currentSubjects = lecture.getLectureSubjects();

            // 1. 현재 과목들을 Map<subjectId, LectureSubjectEntity>로 매핑
            Map<Long, LectureSubjectEntity> currentMap = currentSubjects.stream()
                    .collect(Collectors.toMap(LectureSubjectEntity::getSubjectId, s -> s));

            // 2. 요청 중 업데이트 대상 ID 목록 수집
            Set<Long> incomingIds = newSubjects.stream()
                    .map(LectureSubjectUpdateDto::getSubjectId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 3. 기존 중에서 요청에 포함되지 않은 ID는 삭제
            List<LectureSubjectEntity> toDelete = currentSubjects.stream()
                    .filter(s -> !incomingIds.contains(s.getSubjectId()))
                    .toList();
            lecture.getLectureSubjects().removeAll(toDelete);
            lectureSubjectRepository.deleteAll(toDelete);

            // 4. 새로 추가되거나 제목이 바뀐 과목 처리
            for (LectureSubjectUpdateDto dto : newSubjects) {
                Long subjectId = dto.getSubjectId();
                String title = dto.getSubjectTitle();

                if (subjectId == null) {
                    // → 새로 추가되는 과목
                    LectureSubjectEntity newSubject = LectureSubjectEntity.builder()
                            .lecture(lecture)
                            .subjectTitle(title)
                            .build();
                    lectureSubjectRepository.save(newSubject);
                } else {
                    // → 기존 과목 중 제목이 바뀐 경우 수정
                    LectureSubjectEntity existing = currentMap.get(subjectId);
                    if (existing != null && !existing.getSubjectTitle().equals(title)) {
                        existing.changeSubjectTitle(title);
                    }
                }
            }
        }


        // ✅ 총 수업일수 업데이트
        long totalDays = calculateLectureAllDays(request.getLectureStart(), request.getLectureEnd());
        lecture.changeLectureAllDate(totalDays);
    }



    @Transactional
    public void updateLectureSettings(Long id, LectureSettingsUpdateRequest request) {
        LectureEntity lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.LECTURE_NOT_FOUND));

        if (request.getLectureWarn() != null) {
            lecture.changeWarn(request.getLectureWarn());
        }

        if (request.getLectureDanger() != null) {
            lecture.changeDanger(request.getLectureDanger());
        }

        if (request.getLecturePriority() != null) {
            Long priority = request.getLecturePriority();
            if (priority < 1 || priority > 5) {
                priority = 10L;
            } else {
                validateUniquePriority(priority, id);
            }
            lecture.changePriority(priority);
        }

        if (request.getLectureStatus() != null) {
            lecture.changeStatus(request.getLectureStatus());
        }

        if (request.getLectureLayoutStart() != null) {
//            lecture.changeLayoutStart(LocalDateTime.parse(request.getLectureLayoutStart()));
            lecture.changeLayoutStart(request.getLectureLayoutStart());
        }

        if (request.getLectureLayoutEnd() != null) {
//            lecture.changeLayoutEnd(LocalDateTime.parse(request.getLectureLayoutEnd()));
            lecture.changeLayoutEnd(request.getLectureLayoutEnd());
        }
        // 더티체킹이 적용되므로 save() 호출 불필요
    }


    // ✅ 공통 파일 저장 메서드
//    private String storeFile(MultipartFile file, String folder) {
//        try {
//            String uuid = UUID.randomUUID().toString();
//            String ext = Objects.requireNonNull(file.getOriginalFilename())
//                    .substring(file.getOriginalFilename().lastIndexOf("."));
//            String filename = uuid + ext;
//
//            Path savePath = Paths.get("src/main/resources/static/upload/" + folder + "/" + filename);
//            log.debug("📁 저장 경로: {}", savePath.toAbsolutePath());
//            Files.createDirectories(savePath.getParent());
//            file.transferTo(savePath.toFile());
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


    private void validateUniquePriority(Long priority, Long currentLectureId) {
        // 1, 2, 3, 4번만 중복 검사하고 나머지는 건너뜀
        if (priority < 1 || priority > 5) return;

        boolean exists = lectureRepository.findAll().stream()
                .anyMatch(l -> l.getLecturePriority().equals(priority) && !l.getLectureId().equals(currentLectureId));

        if (exists) {
            throw new IllegalStateException("이미 우선순위 " + priority + "번을 가진 강의가 존재합니다.");
        }
    }

    private long calculateLectureAllDays(LocalDate start, LocalDate end) {
        List<LocalDate> holidays = holidayService.getHolidaysForYearRange(start.getYear(), end.getYear())
                .stream().distinct().toList(); // 중복 방지
        long count = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date)) {
                count++;
            }
        }
        return count;
    }
}
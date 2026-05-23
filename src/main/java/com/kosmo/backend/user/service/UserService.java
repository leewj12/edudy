package com.kosmo.backend.user.service;

import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.exception.ErrorCode;
import com.kosmo.backend.global.jwt.JwtProvider;
import com.kosmo.backend.global.util.HashUtil;
import com.kosmo.backend.user.dto.*;
import com.kosmo.backend.user.entity.Role;
import com.kosmo.backend.user.entity.SnsType;
import com.kosmo.backend.user.entity.UserEntity;
import com.kosmo.backend.user.entity.UserStatus;
import com.kosmo.backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kosmo.backend.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    /**
     * 로그인 검증: 이메일과 비밀번호 일치 확인
     * 성공하면 사용자 email 반환 (→ JWT 생성용)
     */

    @Transactional
    public LoginResponse loginAndIssueTokens(LoginRequest request, HttpServletResponse response) {
        //유저 조회 + 비밀번호 검증
        UserEntity user = login(request.getEmail(), request.getPassword());

        //검증이 끝났다면 accessToken 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        //refreshToken 생성
        String refreshToken = UUID.randomUUID().toString();

        //새로운 리프레시 토큰 저장(갱신)
//        User updatedUser = user.toBuilder()
//                .refreshToken(refreshToken)
//                .refreshTokenExpiry(LocalDateTime.now().plusDays(7))
//                .build();
//        userRepository.save(updatedUser); // 기존 user 대신 업데이트된 user 저장
        // 더티 체킹을 위한 내부 상태 수정
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(7));

        //리프레시 토큰은  쿠키에 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                //쿠키 이름을 "refreshToken", 값은 refreshToken으로 설정
                .httpOnly(true)  //JS에서 쿠키 접근 불가하게 만듦
                .secure(true)    //HTTPS에서만 전송되게 제한 (로컬 개발 중엔 false로도 가능)
                .path("/")       //어떤 경로에서든 쿠키가 포함되도록 설정
                .maxAge(7 * 24 * 60 * 60)   //유효기간: 7일 (초 단위로 설정)
                .sameSite("None") //크로스 도메인 요청에 쿠키 포함 안 함
                .build();        //	최종 쿠키 객체 생성
        response.setHeader("Set-Cookie", cookie.toString());   //직접 쿠키를 응답 헤더에 설정함

        return new LoginResponse(accessToken);
    }

    public UserEntity login(String email , String password){
        //이메일 확인 로직
        UserEntity user = userRepository.findByUserEmail(email)
                .orElseThrow(()->new CustomAuthException(INVALID_CREDENTIALS));

        // ❗️삭제된 사용자 로그인 차단
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomAuthException(USER_DELETED); // 🔒 사용자 삭제 상태
        }

        // ❌ 비활성화 계정 차단
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new CustomAuthException(USER_INACTIVE);
        }

        //비밀번호 확인 로직
        if(!passwordEncoder.matches(password, user.getUserPwd())){
            throw new CustomAuthException(INVALID_CREDENTIALS);
        }
        return user;
    }

    @Transactional
    public LoginResponse adminLogin(LoginRequest request, HttpServletResponse response) {
        UserEntity user = userRepository
                .findByUserEmailAndUserRoleIn(request.getEmail(), List.of(Role.ADMIN, Role.INSTRUCTOR))
                .orElseThrow(() -> new CustomAuthException(ErrorCode.UNAUTHORIZED_ROLE));

        // ❗️삭제된 사용자 로그인 차단
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomAuthException(USER_DELETED); // 🔒 사용자 삭제 상태
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getUserPwd())) {
            throw new CustomAuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(7));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        return new LoginResponse(accessToken);
    }


    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtProvider.resolveToken(request);

        if (token != null && jwtProvider.validateToken(token)) {
            String email = jwtProvider.getEmailFromToken(token);
            UserEntity user = userRepository.findByUserEmail(email)
                    .orElseThrow(() -> new CustomAuthException(USER_NOT_FOUND));

//            User updatedUser = user.toBuilder()
//                    .refreshToken(null)
//                    .refreshTokenExpiry(null)
//                    .build();
//            userRepository.save(updatedUser);
            user.clearRefreshToken(); // 내부 상태만 변경 → 더티 체킹
        }

        // 쿠키 삭제
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

    @Transactional
    public LoginResponse signup(SignupRequest request, HttpServletResponse response) {
        if (userRepository.findByUserEmail(request.getEmail()).isPresent()) {
            throw new CustomAuthException(EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByUserPhone(request.getPhone()).isPresent()) {
            throw new CustomAuthException(ErrorCode.USER_PHONE_ALREADY_EXISTS);
        }

        // 성별 추론
        String gender = extractGenderFromBirthBack(request.getBirthBack());

        // 주민번호 뒷자리 해싱
        String hashedBirthBack = HashUtil.sha256(request.getBirthBack());

        UserEntity user = UserEntity.builder()
                .userEmail(request.getEmail())
                .userPwd(passwordEncoder.encode(request.getPassword()))
                .usersName(request.getName())
                .userGender(gender)
                .userBirth(request.getBirth())
//                .userBirth(LocalDate.parse("1999-02-03"))
                .userBirthBack(hashedBirthBack) // 🔐 해싱된 값 저장
                .userPhone(request.getPhone())
                .userPostcode(request.getPostcode())
                .userAddress(request.getAddress())
                .userAddressDetail(request.getAddressDetail())
                .userGrade(request.getGrade())
                .userMarketing(request.getMarketing())
                .userPrivacy(request.getPrivacy())
                .userSns(request.getSns() != null ? request.getSns() : SnsType.SELF)
                .userStatus(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .userRole(Role.USER)
                .build();

        userRepository.save(user);

        //검증이 끝났다면 accessToken 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        //refreshToken 생성
        String refreshToken = UUID.randomUUID().toString();

        // 더티 체킹을 위한 내부 상태 수정
        user.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(7));

        //리프레시 토큰은  쿠키에 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                //쿠키 이름을 "refreshToken", 값은 refreshToken으로 설정
                .httpOnly(true)  //JS에서 쿠키 접근 불가하게 만듦
                .secure(true)    //HTTPS에서만 전송되게 제한 (로컬 개발 중엔 false로도 가능)
                .path("/")       //어떤 경로에서든 쿠키가 포함되도록 설정
                .maxAge(7 * 24 * 60 * 60)   //유효기간: 7일 (초 단위로 설정)
                .sameSite("None") //크로스 도메인 요청에 쿠키 포함 안 함
                .build();        //   최종 쿠키 객체 생성
        response.setHeader("Set-Cookie", cookie.toString());   //직접 쿠키를 응답 헤더에 설정함

        return new LoginResponse(accessToken);
    }


    public boolean isEmailAlreadyExists(String email) {
        return userRepository.findByUserEmail(email).isPresent();
    }

    private String extractGenderFromBirthBack(String birthBack) {
        if (birthBack == null || birthBack.isBlank() || birthBack.isEmpty()) {
            throw new CustomAuthException(INVALID_BIRTH_BACK);
        }

        char code = birthBack.charAt(0);
        return switch (code) {
            case '1', '3' -> "남자";
            case '2', '4' -> "여자";
            default -> throw new CustomAuthException(GENDER_INFERENCE_FAILED);
        };
    }

    public UserEntity getCurrentUser(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new CustomAuthException(USER_NOT_FOUND));
    }

    public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        String refreshToken = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
                //.orElseThrow(() -> new CustomAuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken == null) return null;

        Optional<UserEntity> optionalUser = userRepository.findByRefreshToken(refreshToken);

        if (optionalUser.isEmpty()) return null;

        UserEntity user = optionalUser.get();

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new CustomAuthException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        return jwtProvider.generateAccessToken(user);
    }

    public UserDTO getMyInfo(HttpServletRequest req) {
        String token = jwtProvider.resolveToken(req);
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new CustomAuthException(ErrorCode.INVALID_TOKEN);
        }
        String email = jwtProvider.getEmailFromToken(token);
        UserEntity user = getCurrentUser(email);
        return new UserDTO(user);
    }

    @Transactional
    public void updateMyInfo(HttpServletRequest req, UserUpdateRequest dto) {


        String token = jwtProvider.resolveToken(req);
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new CustomAuthException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(token);
        UserEntity user = getCurrentUser(email);

        // ✅ 휴대폰 번호 중복 검사
        if (dto.getUserPhone() != null && !dto.getUserPhone().equals(user.getUserPhone())) {
            boolean phoneInUse = userRepository.findByUserPhoneAndUserIdNot(dto.getUserPhone(), user.getUserId()).isPresent();
            if (phoneInUse) {
                throw new CustomAuthException(ErrorCode.USER_PHONE_ALREADY_EXISTS);
            }
        }

        // ✅ 정보 업데이트
        user.updateUserInfo(dto);  // entity의 메서드 사용

        // 비밀번호 변경 요청이 있는 경우만
        if (dto.getUserPwd() != null && !dto.getUserPwd().isBlank()) {
            String encoded = passwordEncoder.encode(dto.getUserPwd());
            user.updatePassword(encoded);
        }
    }

    @Transactional
    public void changePassword(HttpServletRequest req, PasswordChangeRequest request) {
        String token = jwtProvider.resolveToken(req);
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new CustomAuthException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(token);
        UserEntity user = getCurrentUser(email);

        // 비밀번호 인코딩 후 저장
        String encoded = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encoded);
    }

    @Transactional
    public void deleteMyAccount(HttpServletRequest req) {
        String token = jwtProvider.resolveToken(req);
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new CustomAuthException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(token);
        UserEntity user = getCurrentUser(email);

        user.updateStatus(UserStatus.DELETED); // 상태만 변경
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));

        user.updateStatus(UserStatus.DELETED); // 상태만 변경
    }

    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserListResponse.builder()
                        .userId(user.getUserId())
                        .usersName(user.getUsersName())
                        .userEmail(user.getUserEmail())
                        .userPhone(user.getUserPhone())
                        .userGender(user.getUserGender())
                        .userBirth(user.getUserBirth())
                        .userRole(user.getUserRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));
        user.updateRole(newRole);
    }

    public List<UserInstructorDto> getAllInstructors() {
        return userRepository.findByUserRole(Role.INSTRUCTOR).stream()
                .map(user -> new UserInstructorDto(
                        user.getUserId(),
                        user.getUsersName(),
                        user.getUserEmail(),     // ✅ 이메일 추가
                        user.getUserBirth(),     // ✅ 생년월일 추가
                        user.getUserPhone()      // ✅ 전화번호 추가
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public LoginResponse uploadUserSign(HttpServletRequest req, MultipartFile file) {
        String token = jwtProvider.resolveToken(req);
        if (token == null || !jwtProvider.validateToken(token)) {
            throw new CustomAuthException(ErrorCode.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmailFromToken(token);
        UserEntity user = getCurrentUser(email);

        // 권한 체크
        if (!(user.getUserRole() == Role.INSTRUCTOR || user.getUserRole() == Role.ADMIN)) {
            throw new CustomAuthException(ErrorCode.UNAUTHORIZED_ROLE);
        }

        // 이미지 저장
        String savedFilename = storeFile(file, "sign");

        // 엔티티에 반영 (파일명만 저장)
        user.updateUserSign(savedFilename);

        // ✅ 변경된 정보로 AccessToken 재발급
        String newAccessToken = jwtProvider.generateAccessToken(user);

        return new LoginResponse(newAccessToken);
    }

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

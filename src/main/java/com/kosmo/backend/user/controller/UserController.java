package com.kosmo.backend.user.controller;


import com.kosmo.backend.global.exception.CustomAuthException;
import com.kosmo.backend.global.jwt.JwtProvider;
import com.kosmo.backend.user.dto.*;
import com.kosmo.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

//    @GetMapping("/me")
//    public ResponseEntity<?> getUserInfo(HttpServletRequest req){
//        // 헤더에서 토큰 추출
//        String token = jwtProvider.resolveToken(req);
//
//        //만약 토큰이 있거나 유효한 토큰일 경우에 사용자 정보를 가져온다
//        if (token != null && jwtProvider.validateToken(token)) {
//            String email = jwtProvider.getEmailFromToken(token);
//            User user = userService.getCurrentUser(email);
//            return ResponseEntity.ok(user);
//        } else {
//            return ResponseEntity.status(401).body("유효하지 않은 토큰");
//        }
//    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getUserInfo(HttpServletRequest req) {
        try {
            return ResponseEntity.ok(userService.getMyInfo(req));
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @PatchMapping("/user/me")
    public ResponseEntity<?> updateUserInfo(
            HttpServletRequest req,
            @RequestBody UserUpdateRequest dto
    ) {
        try {
            userService.updateMyInfo(req, dto);
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @PatchMapping("/user/me/password")
    public ResponseEntity<?> changePassword(
            HttpServletRequest req,
            @RequestBody PasswordChangeRequest request
    ) {
        try {
            userService.changePassword(req, request);
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @DeleteMapping("/user/me/delete")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest req) {
        try {
            userService.deleteMyAccount(req);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/users/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }

    @GetMapping("/admin/users/list")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<UserListResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/admin/users/update/{userId}")
    public ResponseEntity<String> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest request
    ) {
        userService.updateUserRole(userId, request.getNewRole());
        return ResponseEntity.ok("권한이 성공적으로 변경되었습니다.");
    }

    @GetMapping("/admin/instructor/list")
    public ResponseEntity<List<UserInstructorDto>> getInstructorList() {
        List<UserInstructorDto> instructors = userService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    // 관리자용 sing 업로드 + accessToken 재발급
    @PatchMapping("/admin/me/sign")
    public ResponseEntity<?> uploadUserSign(
            HttpServletRequest req,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            LoginResponse response = userService.uploadUserSign(req, file);
            return ResponseEntity.ok(response); // 새로운 accessToken 포함
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    // 강사용 sign 업로드 + accessToken 재발급
    @PatchMapping("/instr/me/sign")
    public ResponseEntity<?> uploadUserSignInstr(
            HttpServletRequest req,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            LoginResponse response = userService.uploadUserSign(req, file);
            return ResponseEntity.ok(response); // 새로운 accessToken 포함
        } catch (CustomAuthException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

}

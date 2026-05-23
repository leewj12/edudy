package com.kosmo.backend.user.controller;

import com.kosmo.backend.global.jwt.JwtProvider;
import com.kosmo.backend.user.dto.LoginRequest;
import com.kosmo.backend.user.dto.LoginResponse;
import com.kosmo.backend.user.dto.SignupRequest;
import com.kosmo.backend.user.repository.UserRepository;
import com.kosmo.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@RequestBody @Valid LoginRequest request , HttpServletResponse response){
        return ResponseEntity.ok(userService.loginAndIssueTokens(request, response));
    }

    @PostMapping("/adminLogin")
    public ResponseEntity<LoginResponse> adminLogin(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(userService.adminLogin(request, response));
    }

    @GetMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String newAccessToken = userService.reissueAccessToken(request, response);

        if (newAccessToken == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        // 5. 응답
        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.signup(request, response));
    }

    @GetMapping("/emailAlreadyExists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean exists = userService.isEmailAlreadyExists(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok("로그아웃 완료");
    }


}

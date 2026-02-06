package com.f1gg.backend.auth;

import com.f1gg.backend.auth.dto.*;
import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseData<TokenResponse.UserInfo>> signup(@RequestBody SignupRequest request) {
        ResponseData<TokenResponse.UserInfo> response = authService.signup(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseData<TokenResponse>> login(@RequestBody LoginRequest request) {
        ResponseData<TokenResponse> response = authService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<TokenResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        ResponseData<TokenResponse> response = authService.refreshToken(refreshToken);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(@AuthenticationPrincipal String email) {
        ResponseData<Void> response = authService.logout(email);
        return ResponseEntity.ok(response);
    }

    // 내 정보 조회 (인증 필요)
    @GetMapping("/me")
    public ResponseEntity<ResponseData<Map<String, String>>> me(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(401).body(ResponseData.unauthorized("인증이 필요합니다."));
        }
        return ResponseEntity.ok(ResponseData.success(Map.of("email", email)));
    }
}

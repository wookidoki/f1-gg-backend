package com.f1gg.backend.auth;

import com.f1gg.backend.auth.dto.*;
import com.f1gg.backend.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ResponseData<TokenResponse.UserInfo>> signup(@RequestBody SignupRequest request) {
        ResponseData<TokenResponse.UserInfo> response = authService.signup(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ResponseData<TokenResponse>> login(@RequestBody LoginRequest request) {
        ResponseData<TokenResponse> response = authService.login(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 Access Token을 갱신합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<TokenResponse>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        ResponseData<TokenResponse> response = authService.refreshToken(refreshToken);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(@AuthenticationPrincipal String email) {
        ResponseData<Void> response = authService.logout(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ResponseData<Map<String, String>>> me(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(401).body(ResponseData.unauthorized("인증이 필요합니다."));
        }
        return ResponseEntity.ok(ResponseData.success(Map.of("email", email)));
    }
}

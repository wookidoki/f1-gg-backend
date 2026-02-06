package com.f1gg.backend.auth;

import com.f1gg.backend.auth.dto.*;
import com.f1gg.backend.auth.entity.User;
import com.f1gg.backend.common.ResponseData;
import com.f1gg.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // TODO: 실제 DB 연동 시 Mapper로 교체
    private final Map<String, User> userStore = new ConcurrentHashMap<>();
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 회원가입
    public ResponseData<TokenResponse.UserInfo> signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userStore.containsKey(request.getEmail())) {
            return ResponseData.badRequest("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화 및 사용자 생성
        User user = User.builder()
                .id(idGenerator.getAndIncrement())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        userStore.put(user.getEmail(), user);
        log.info("회원가입 완료: {}", user.getEmail());

        TokenResponse.UserInfo userInfo = TokenResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();

        return ResponseData.created(userInfo);
    }

    // 로그인
    public ResponseData<TokenResponse> login(LoginRequest request) {
        User user = userStore.get(request.getEmail());

        // 사용자 존재 확인
        if (user == null) {
            return ResponseData.unauthorized("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseData.unauthorized("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Refresh Token 저장
        refreshTokenStore.put(user.getEmail(), refreshToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1시간
                .user(TokenResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .build())
                .build();

        log.info("로그인 성공: {}", user.getEmail());
        return ResponseData.success(tokenResponse, "로그인 성공");
    }

    // 토큰 갱신
    public ResponseData<TokenResponse> refreshToken(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseData.unauthorized("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        // 저장된 Refresh Token과 비교
        String storedToken = refreshTokenStore.get(email);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return ResponseData.unauthorized("Refresh Token이 일치하지 않습니다.");
        }

        User user = userStore.get(email);
        if (user == null) {
            return ResponseData.unauthorized("사용자를 찾을 수 없습니다.");
        }

        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        // Refresh Token 갱신
        refreshTokenStore.put(email, newRefreshToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .user(TokenResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .build())
                .build();

        return ResponseData.success(tokenResponse, "토큰 갱신 성공");
    }

    // 로그아웃
    public ResponseData<Void> logout(String email) {
        refreshTokenStore.remove(email);
        log.info("로그아웃: {}", email);
        return ResponseData.success(null, "로그아웃 성공");
    }
}

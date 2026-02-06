package com.f1gg.backend.favorite;

import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 목록 조회
    @GetMapping
    public ResponseEntity<ResponseData<FavoriteListResponse>> getFavorites(
            @AuthenticationPrincipal String email) {
        ResponseData<FavoriteListResponse> response = favoriteService.getFavorites(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 즐겨찾기 추가
    @PostMapping
    public ResponseEntity<ResponseData<FavoriteItem>> addFavorite(
            @AuthenticationPrincipal String email,
            @RequestBody FavoriteRequest request) {
        ResponseData<FavoriteItem> response = favoriteService.addFavorite(
                email, request.getType(), request.getTargetId());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<ResponseData<Void>> removeFavorite(
            @AuthenticationPrincipal String email,
            @RequestParam String type,
            @RequestParam String targetId) {
        ResponseData<Void> response = favoriteService.removeFavorite(email, type, targetId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 즐겨찾기 여부 확인
    @GetMapping("/check")
    public ResponseEntity<ResponseData<Map<String, Boolean>>> checkFavorite(
            @AuthenticationPrincipal String email,
            @RequestParam String type,
            @RequestParam String targetId) {
        ResponseData<Map<String, Boolean>> response = favoriteService.checkFavorite(email, type, targetId);
        return ResponseEntity.ok(response);
    }

    @lombok.Data
    public static class FavoriteRequest {
        private String type;      // DRIVER or CONSTRUCTOR
        private String targetId;  // driver code or constructor id
    }
}

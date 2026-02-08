package com.f1gg.backend.favorite;

import com.f1gg.backend.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Favorite", description = "즐겨찾기 API (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "즐겨찾기 목록 조회", description = "사용자의 즐겨찾기 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseData<FavoriteListResponse>> getFavorites(
            @AuthenticationPrincipal String email) {
        ResponseData<FavoriteListResponse> response = favoriteService.getFavorites(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "즐겨찾기 추가", description = "드라이버 또는 팀을 즐겨찾기에 추가합니다.")
    @PostMapping
    public ResponseEntity<ResponseData<FavoriteItem>> addFavorite(
            @AuthenticationPrincipal String email,
            @RequestBody FavoriteRequest request) {
        ResponseData<FavoriteItem> response = favoriteService.addFavorite(
                email, request.getType(), request.getTargetId());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기에서 항목을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ResponseData<Void>> removeFavorite(
            @AuthenticationPrincipal String email,
            @Parameter(description = "타입 (DRIVER 또는 CONSTRUCTOR)", example = "DRIVER")
            @RequestParam String type,
            @Parameter(description = "대상 ID", example = "VER")
            @RequestParam String targetId) {
        ResponseData<Void> response = favoriteService.removeFavorite(email, type, targetId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "즐겨찾기 여부 확인", description = "특정 항목이 즐겨찾기에 있는지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<ResponseData<Map<String, Boolean>>> checkFavorite(
            @AuthenticationPrincipal String email,
            @Parameter(description = "타입 (DRIVER 또는 CONSTRUCTOR)", example = "DRIVER")
            @RequestParam String type,
            @Parameter(description = "대상 ID", example = "VER")
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

package com.f1gg.backend.favorite;

import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    // 사용자별 즐겨찾기 저장소: email -> Set<FavoriteItem>
    private final Map<String, Set<FavoriteItem>> favoriteStore = new ConcurrentHashMap<>();

    // 즐겨찾기 추가
    public ResponseData<FavoriteItem> addFavorite(String email, String type, String targetId) {
        if (email == null || email.isBlank()) {
            return ResponseData.unauthorized("로그인이 필요합니다.");
        }

        FavoriteItem item = FavoriteItem.builder()
                .type(type.toUpperCase())
                .targetId(targetId)
                .createdAt(System.currentTimeMillis())
                .build();

        favoriteStore.computeIfAbsent(email, k -> ConcurrentHashMap.newKeySet());
        Set<FavoriteItem> favorites = favoriteStore.get(email);

        // 이미 존재하는지 확인
        boolean exists = favorites.stream()
                .anyMatch(f -> f.getType().equals(item.getType()) && f.getTargetId().equals(item.getTargetId()));

        if (exists) {
            return ResponseData.badRequest("이미 즐겨찾기에 추가되어 있습니다.");
        }

        favorites.add(item);
        log.info("즐겨찾기 추가: {} -> {} {}", email, type, targetId);

        return ResponseData.created(item);
    }

    // 즐겨찾기 삭제
    public ResponseData<Void> removeFavorite(String email, String type, String targetId) {
        if (email == null || email.isBlank()) {
            return ResponseData.unauthorized("로그인이 필요합니다.");
        }

        Set<FavoriteItem> favorites = favoriteStore.get(email);
        if (favorites == null) {
            return ResponseData.notFound("즐겨찾기가 없습니다.");
        }

        boolean removed = favorites.removeIf(f ->
                f.getType().equalsIgnoreCase(type) && f.getTargetId().equals(targetId));

        if (!removed) {
            return ResponseData.notFound("해당 즐겨찾기를 찾을 수 없습니다.");
        }

        log.info("즐겨찾기 삭제: {} -> {} {}", email, type, targetId);
        return ResponseData.success(null, "즐겨찾기가 삭제되었습니다.");
    }

    // 사용자의 즐겨찾기 목록 조회
    public ResponseData<FavoriteListResponse> getFavorites(String email) {
        if (email == null || email.isBlank()) {
            return ResponseData.unauthorized("로그인이 필요합니다.");
        }

        Set<FavoriteItem> favorites = favoriteStore.getOrDefault(email, Set.of());

        List<FavoriteItem> drivers = favorites.stream()
                .filter(f -> "DRIVER".equals(f.getType()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .collect(Collectors.toList());

        List<FavoriteItem> constructors = favorites.stream()
                .filter(f -> "CONSTRUCTOR".equals(f.getType()))
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .collect(Collectors.toList());

        FavoriteListResponse response = FavoriteListResponse.builder()
                .drivers(drivers)
                .constructors(constructors)
                .totalCount(favorites.size())
                .build();

        return ResponseData.success(response, "즐겨찾기 목록 조회 성공");
    }

    // 특정 항목이 즐겨찾기인지 확인
    public ResponseData<Map<String, Boolean>> checkFavorite(String email, String type, String targetId) {
        if (email == null || email.isBlank()) {
            return ResponseData.success(Map.of("isFavorite", false));
        }

        Set<FavoriteItem> favorites = favoriteStore.getOrDefault(email, Set.of());
        boolean isFavorite = favorites.stream()
                .anyMatch(f -> f.getType().equalsIgnoreCase(type) && f.getTargetId().equals(targetId));

        return ResponseData.success(Map.of("isFavorite", isFavorite));
    }
}

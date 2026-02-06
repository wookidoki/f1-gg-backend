package com.f1gg.backend.favorite;

import lombok.Builder;
import lombok.Data;
import java.util.Objects;

@Data
@Builder
public class FavoriteItem {
    private String type;      // DRIVER or CONSTRUCTOR
    private String targetId;  // driver code (e.g., "VER") or constructor id (e.g., "red_bull")
    private long createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteItem that = (FavoriteItem) o;
        return Objects.equals(type, that.type) && Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, targetId);
    }
}

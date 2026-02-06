package com.f1gg.backend.favorite;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FavoriteListResponse {
    private List<FavoriteItem> drivers;
    private List<FavoriteItem> constructors;
    private int totalCount;
}

package com.f1gg.backend.constructor;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConstructorResponse {
    private int rank;
    private String constructorId;
    private String name;
    private String nameKr;
    private String nationality;
    private String color;
    private String points;
    private String wins;

    // 소속 드라이버 (간략)
    private List<DriverSummary> drivers;

    @Data
    @Builder
    public static class DriverSummary {
        private String code;
        private String nameKr;
        private String nameEn;
        private String number;
    }
}

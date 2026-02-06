package com.f1gg.backend.constructor;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConstructorDetailResponse {
    private String constructorId;
    private String name;
    private String nameKr;
    private String nationality;
    private String color;

    // 현재 시즌 성적
    private int currentRank;
    private String currentPoints;
    private String currentWins;

    // 커리어 통계
    private CareerStats careerStats;

    // 소속 드라이버
    private List<DriverInfo> drivers;

    // 현재 시즌 경기 결과
    private List<RaceResult> seasonResults;

    @Data
    @Builder
    public static class CareerStats {
        private int championships;
        private int wins;
        private int seasons;
    }

    @Data
    @Builder
    public static class DriverInfo {
        private String driverId;
        private String code;
        private String number;
        private String nameKr;
        private String nameEn;
        private String nationality;
        private int rank;
        private String points;
    }

    @Data
    @Builder
    public static class RaceResult {
        private int round;
        private String raceName;
        private String raceNameKr;
        private String date;
        private String points;
        private List<DriverResult> driverResults;
    }

    @Data
    @Builder
    public static class DriverResult {
        private String code;
        private int position;
        private String points;
    }
}

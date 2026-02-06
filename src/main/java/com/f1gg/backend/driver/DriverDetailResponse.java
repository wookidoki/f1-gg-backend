package com.f1gg.backend.driver;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DriverDetailResponse {
    // 기본 정보
    private String driverId;
    private String code;
    private String number;
    private String nameKr;
    private String nameEn;
    private String nationality;
    private String dob;

    // 현재 팀 정보
    private String team;
    private String teamColor;

    // 현재 시즌 성적
    private int currentRank;
    private String currentPoints;
    private String currentWins;

    // 커리어 통계
    private CareerStats careerStats;

    // 현재 시즌 경기별 결과
    private List<RaceResult> seasonResults;

    @Data
    @Builder
    public static class CareerStats {
        private int championships;  // 월드 챔피언 횟수
        private int wins;           // 총 우승
        private int podiums;        // 총 포디움
        private int poles;          // 총 폴 포지션 (API에서 제공 안함, 0으로)
        private int entries;        // 총 출전 횟수
        private int seasons;        // 참가 시즌 수
    }

    @Data
    @Builder
    public static class RaceResult {
        private int round;
        private String raceName;
        private String raceNameKr;
        private String date;
        private int position;
        private String points;
        private int grid;           // 출발 그리드
        private String status;      // Finished, +1 Lap, Retired 등
    }
}

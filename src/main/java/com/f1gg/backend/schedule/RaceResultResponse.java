package com.f1gg.backend.schedule;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RaceResultResponse {
    private String season;
    private int round;
    private String raceName;
    private String raceNameKr;
    private String circuit;
    private String circuitKr;
    private String country;
    private String countryFlag;
    private String date;
    private List<Result> results;

    @Data
    @Builder
    public static class Result {
        private int position;
        private String driverId;
        private String code;
        private String number;
        private String nameKr;
        private String nameEn;
        private String team;
        private String teamColor;
        private int grid;
        private int laps;
        private String time;
        private String status;
        private String points;
        private boolean fastestLap;
    }
}

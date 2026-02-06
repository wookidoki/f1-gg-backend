package com.f1gg.backend.standings;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StandingsResponse {

    @Data
    @Builder
    public static class DriverStandings {
        private String season;
        private int round;
        private List<DriverEntry> standings;
    }

    @Data
    @Builder
    public static class DriverEntry {
        private int position;
        private String points;
        private String wins;
        private Driver driver;
        private Constructor constructor;
    }

    @Data
    @Builder
    public static class Driver {
        private String driverId;
        private String code;
        private String number;
        private String nameKr;
        private String nameEn;
        private String nationality;
    }

    @Data
    @Builder
    public static class Constructor {
        private String constructorId;
        private String name;
        private String nameKr;
        private String color;
    }

    @Data
    @Builder
    public static class ConstructorStandings {
        private String season;
        private int round;
        private List<ConstructorEntry> standings;
    }

    @Data
    @Builder
    public static class ConstructorEntry {
        private int position;
        private String points;
        private String wins;
        private Constructor constructor;
    }
}

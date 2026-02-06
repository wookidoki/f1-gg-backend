package com.f1gg.backend.schedule;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ScheduleResponse {
    private String season;
    private int totalRaces;
    private List<Race> races;

    @Data
    @Builder
    public static class Race {
        private int round;
        private String raceName;
        private String raceNameKr;
        private String circuit;
        private String circuitKr;
        private String country;
        private String countryFlag;
        private String date;
        private String time;
        private String status; // FINISHED, UPCOMING
        private Winner winner; // null if UPCOMING
    }

    @Data
    @Builder
    public static class Winner {
        private String code;
        private String nameKr;
        private String nameEn;
        private String team;
    }
}

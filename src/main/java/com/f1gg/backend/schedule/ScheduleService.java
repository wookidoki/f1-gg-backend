package com.f1gg.backend.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import com.f1gg.backend.api.JolpicaClient;
import com.f1gg.backend.api.TranslationService;
import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final JolpicaClient jolpicaClient;
    private final TranslationService translationService;

    // 팀 컬러 매핑
    private final Map<String, String> colorMap = Map.of(
        "Red Bull", "#0600EF",
        "McLaren", "#FF8000",
        "Ferrari", "#C00000",
        "Mercedes", "#00D2BE",
        "Aston Martin", "#006F62",
        "RB F1 Team", "#6692FF",
        "Haas F1 Team", "#B6BABD",
        "Williams", "#64C4FF",
        "Alpine F1 Team", "#0090FF",
        "Kick Sauber", "#52E252"
    );

    // 그랑프리 한글명 매핑
    private final Map<String, String> raceNameKrMap = Map.ofEntries(
        Map.entry("Bahrain Grand Prix", "바레인 GP"),
        Map.entry("Saudi Arabian Grand Prix", "사우디아라비아 GP"),
        Map.entry("Australian Grand Prix", "호주 GP"),
        Map.entry("Japanese Grand Prix", "일본 GP"),
        Map.entry("Chinese Grand Prix", "중국 GP"),
        Map.entry("Miami Grand Prix", "마이애미 GP"),
        Map.entry("Emilia Romagna Grand Prix", "에밀리아 로마냐 GP"),
        Map.entry("Monaco Grand Prix", "모나코 GP"),
        Map.entry("Canadian Grand Prix", "캐나다 GP"),
        Map.entry("Spanish Grand Prix", "스페인 GP"),
        Map.entry("Austrian Grand Prix", "오스트리아 GP"),
        Map.entry("British Grand Prix", "영국 GP"),
        Map.entry("Hungarian Grand Prix", "헝가리 GP"),
        Map.entry("Belgian Grand Prix", "벨기에 GP"),
        Map.entry("Dutch Grand Prix", "네덜란드 GP"),
        Map.entry("Italian Grand Prix", "이탈리아 GP"),
        Map.entry("Azerbaijan Grand Prix", "아제르바이잔 GP"),
        Map.entry("Singapore Grand Prix", "싱가포르 GP"),
        Map.entry("United States Grand Prix", "미국 GP"),
        Map.entry("Mexico City Grand Prix", "멕시코시티 GP"),
        Map.entry("São Paulo Grand Prix", "상파울루 GP"),
        Map.entry("Las Vegas Grand Prix", "라스베가스 GP"),
        Map.entry("Qatar Grand Prix", "카타르 GP"),
        Map.entry("Abu Dhabi Grand Prix", "아부다비 GP")
    );

    // 서킷 한글명 매핑
    private final Map<String, String> circuitKrMap = Map.ofEntries(
        Map.entry("Bahrain International Circuit", "바레인 국제 서킷"),
        Map.entry("Jeddah Corniche Circuit", "제다 코르니쉬 서킷"),
        Map.entry("Albert Park Grand Prix Circuit", "앨버트 파크 서킷"),
        Map.entry("Suzuka Circuit", "스즈카 서킷"),
        Map.entry("Shanghai International Circuit", "상하이 국제 서킷"),
        Map.entry("Miami International Autodrome", "마이애미 국제 오토드롬"),
        Map.entry("Autodromo Enzo e Dino Ferrari", "이몰라 서킷"),
        Map.entry("Circuit de Monaco", "모나코 서킷"),
        Map.entry("Circuit Gilles Villeneuve", "질 빌뇌브 서킷"),
        Map.entry("Circuit de Barcelona-Catalunya", "바르셀로나 카탈루냐 서킷"),
        Map.entry("Red Bull Ring", "레드불 링"),
        Map.entry("Silverstone Circuit", "실버스톤 서킷"),
        Map.entry("Hungaroring", "헝가로링"),
        Map.entry("Circuit de Spa-Francorchamps", "스파 프랑코르샹 서킷"),
        Map.entry("Circuit Park Zandvoort", "잔트포르트 서킷"),
        Map.entry("Autodromo Nazionale di Monza", "몬자 서킷"),
        Map.entry("Baku City Circuit", "바쿠 시티 서킷"),
        Map.entry("Marina Bay Street Circuit", "마리나 베이 서킷"),
        Map.entry("Circuit of the Americas", "아메리카 서킷"),
        Map.entry("Autódromo Hermanos Rodríguez", "에르마노스 로드리게스 서킷"),
        Map.entry("Autódromo José Carlos Pace", "인테를라고스 서킷"),
        Map.entry("Las Vegas Strip Street Circuit", "라스베가스 스트립 서킷"),
        Map.entry("Lusail International Circuit", "루사일 국제 서킷"),
        Map.entry("Yas Marina Circuit", "야스 마리나 서킷")
    );

    // 국가 국기 매핑
    private final Map<String, String> countryFlagMap = Map.ofEntries(
        Map.entry("Bahrain", "🇧🇭"),
        Map.entry("Saudi Arabia", "🇸🇦"),
        Map.entry("Australia", "🇦🇺"),
        Map.entry("Japan", "🇯🇵"),
        Map.entry("China", "🇨🇳"),
        Map.entry("USA", "🇺🇸"),
        Map.entry("Italy", "🇮🇹"),
        Map.entry("Monaco", "🇲🇨"),
        Map.entry("Canada", "🇨🇦"),
        Map.entry("Spain", "🇪🇸"),
        Map.entry("Austria", "🇦🇹"),
        Map.entry("UK", "🇬🇧"),
        Map.entry("Hungary", "🇭🇺"),
        Map.entry("Belgium", "🇧🇪"),
        Map.entry("Netherlands", "🇳🇱"),
        Map.entry("Azerbaijan", "🇦🇿"),
        Map.entry("Singapore", "🇸🇬"),
        Map.entry("Mexico", "🇲🇽"),
        Map.entry("Brazil", "🇧🇷"),
        Map.entry("Qatar", "🇶🇦"),
        Map.entry("UAE", "🇦🇪")
    );

    // 드라이버 한글명 캐시
    private final Map<String, String> driverNameKrCache = new HashMap<>(Map.of(
        "Max Verstappen", "막스 베르스타펜",
        "Lewis Hamilton", "루이스 해밀턴",
        "Fernando Alonso", "페르난도 알론소",
        "Lando Norris", "란도 노리스",
        "Charles Leclerc", "샤를 르클레르",
        "Carlos Sainz", "카를로스 사인츠",
        "George Russell", "조지 러셀",
        "Oscar Piastri", "오스카 피아스트리",
        "Sergio Perez", "세르히오 페레스"
    ));

    // 시즌 일정 조회
    public ScheduleResponse getSchedule(String season) {
        String targetSeason = (season == null || season.isBlank()) ? JolpicaClient.DEFAULT_SEASON : season;
        JsonNode root = jolpicaClient.getSeasonSchedule(targetSeason);
        List<ScheduleResponse.Race> races = new ArrayList<>();

        if (root == null || !root.has("MRData")) {
            return ScheduleResponse.builder()
                    .season(targetSeason).totalRaces(0).races(races).build();
        }

        JsonNode raceTable = root.path("MRData").path("RaceTable");
        String seasonFromApi = raceTable.path("season").asText();
        JsonNode raceList = raceTable.path("Races");

        LocalDate today = LocalDate.now();

        if (raceList.isArray()) {
            for (JsonNode race : raceList) {
                String raceName = race.path("raceName").asText();
                String circuit = race.path("Circuit").path("circuitName").asText();
                String country = race.path("Circuit").path("Location").path("country").asText();
                String dateStr = race.path("date").asText();
                LocalDate raceDate = LocalDate.parse(dateStr);
                int round = race.path("round").asInt();

                String status = raceDate.isBefore(today) ? "FINISHED" : "UPCOMING";
                ScheduleResponse.Winner winner = null;

                // 완료된 경기는 우승자 조회
                if (status.equals("FINISHED")) {
                    winner = getWinner(targetSeason, round);
                }

                races.add(ScheduleResponse.Race.builder()
                        .round(round)
                        .raceName(raceName)
                        .raceNameKr(raceNameKrMap.getOrDefault(raceName, raceName))
                        .circuit(circuit)
                        .circuitKr(circuitKrMap.getOrDefault(circuit, circuit))
                        .country(country)
                        .countryFlag(countryFlagMap.getOrDefault(country, "🏁"))
                        .date(dateStr)
                        .time(race.path("time").asText(""))
                        .status(status)
                        .winner(winner)
                        .build());
            }
        }

        return ScheduleResponse.builder()
                .season(seasonFromApi)
                .totalRaces(races.size())
                .races(races)
                .build();
    }

    // 다음 경기 조회
    public ResponseEntity<ResponseData<?>> getNextRace(String season) {
        ScheduleResponse schedule = getSchedule(season);
        Optional<ScheduleResponse.Race> nextRace = schedule.getRaces().stream()
                .filter(r -> "UPCOMING".equals(r.getStatus()))
                .findFirst();

        if (nextRace.isEmpty()) {
            return ResponseEntity.ok(ResponseData.success(
                Map.of("message", "시즌 종료", "season", schedule.getSeason()),
                "시즌이 종료되었습니다"
            ));
        }

        ScheduleResponse.Race race = nextRace.get();
        LocalDate raceDate = LocalDate.parse(race.getDate());
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), raceDate);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("round", race.getRound());
        result.put("raceName", race.getRaceName());
        result.put("raceNameKr", race.getRaceNameKr());
        result.put("circuit", race.getCircuit());
        result.put("circuitKr", race.getCircuitKr());
        result.put("date", race.getDate());
        result.put("time", race.getTime());
        result.put("countryFlag", race.getCountryFlag());
        result.put("daysUntil", daysUntil);

        return ResponseEntity.ok(ResponseData.success(result, "다음 경기 조회 성공"));
    }

    // 경기 결과 조회
    public ResponseEntity<ResponseData<?>> getRaceResult(String season, int round) {
        String targetSeason = (season == null || season.isBlank()) ? JolpicaClient.DEFAULT_SEASON : season;
        JsonNode root = jolpicaClient.getRaceResults(targetSeason, round);

        if (root == null || !root.has("MRData")) {
            return ResponseEntity.status(404)
                    .body(ResponseData.notFound("경기를 찾을 수 없습니다: Round " + round));
        }

        JsonNode races = root.path("MRData").path("RaceTable").path("Races");
        if (!races.isArray() || races.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseData.notFound("경기 결과가 없습니다: Round " + round));
        }

        JsonNode race = races.get(0);
        String raceName = race.path("raceName").asText();
        String circuit = race.path("Circuit").path("circuitName").asText();
        String country = race.path("Circuit").path("Location").path("country").asText();

        JsonNode results = race.path("Results");
        List<RaceResultResponse.Result> resultList = new ArrayList<>();

        // 가장 빠른 랩 드라이버 찾기
        String fastestLapDriver = null;
        if (results.isArray()) {
            for (JsonNode r : results) {
                if (r.has("FastestLap") && r.path("FastestLap").path("rank").asInt() == 1) {
                    fastestLapDriver = r.path("Driver").path("code").asText();
                    break;
                }
            }
        }

        if (results.isArray()) {
            for (JsonNode r : results) {
                JsonNode driver = r.path("Driver");
                JsonNode constructor = r.path("Constructor");

                String givenName = driver.path("givenName").asText();
                String familyName = driver.path("familyName").asText();
                String fullName = givenName + " " + familyName;
                String code = driver.path("code").asText();
                String teamName = constructor.path("name").asText();

                String nameKr = driverNameKrCache.getOrDefault(fullName,
                    translationService.translateToKorean(fullName));
                if (!driverNameKrCache.containsKey(fullName)) {
                    driverNameKrCache.put(fullName, nameKr);
                }

                String time = r.has("Time") ? r.path("Time").path("time").asText() : r.path("status").asText();

                resultList.add(RaceResultResponse.Result.builder()
                        .position(r.path("position").asInt())
                        .driverId(driver.path("driverId").asText())
                        .code(code)
                        .number(driver.path("permanentNumber").asText())
                        .nameKr(nameKr)
                        .nameEn(fullName)
                        .team(teamName)
                        .teamColor(colorMap.getOrDefault(teamName, "#333333"))
                        .grid(r.path("grid").asInt())
                        .laps(r.path("laps").asInt())
                        .time(time)
                        .status(r.path("status").asText())
                        .points(r.path("points").asText())
                        .fastestLap(code.equals(fastestLapDriver))
                        .build());
            }
        }

        RaceResultResponse response = RaceResultResponse.builder()
                .season(root.path("MRData").path("RaceTable").path("season").asText())
                .round(round)
                .raceName(raceName)
                .raceNameKr(raceNameKrMap.getOrDefault(raceName, raceName))
                .circuit(circuit)
                .circuitKr(circuitKrMap.getOrDefault(circuit, circuit))
                .country(country)
                .countryFlag(countryFlagMap.getOrDefault(country, "🏁"))
                .date(race.path("date").asText())
                .results(resultList)
                .build();

        return ResponseEntity.ok(ResponseData.success(response, "경기 결과 조회 성공"));
    }

    // 우승자 조회 헬퍼
    private ScheduleResponse.Winner getWinner(String season, int round) {
        try {
            JsonNode root = jolpicaClient.getRaceResults(season, round);
            if (root == null || !root.has("MRData")) return null;

            JsonNode races = root.path("MRData").path("RaceTable").path("Races");
            if (!races.isArray() || races.isEmpty()) return null;

            JsonNode results = races.get(0).path("Results");
            if (!results.isArray() || results.isEmpty()) return null;

            JsonNode winner = results.get(0);
            JsonNode driver = winner.path("Driver");
            JsonNode constructor = winner.path("Constructor");

            String givenName = driver.path("givenName").asText();
            String familyName = driver.path("familyName").asText();
            String fullName = givenName + " " + familyName;

            String nameKr = driverNameKrCache.getOrDefault(fullName,
                translationService.translateToKorean(fullName));

            return ScheduleResponse.Winner.builder()
                    .code(driver.path("code").asText())
                    .nameKr(nameKr)
                    .nameEn(fullName)
                    .team(constructor.path("name").asText())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    // 사용 가능한 시즌 목록 조회
    public ResponseEntity<ResponseData<?>> getAvailableSeasons() {
        JsonNode root = jolpicaClient.getAvailableSeasons();

        if (root == null || !root.has("MRData")) {
            return ResponseEntity.ok(ResponseData.success(
                Map.of("seasons", List.of("2024", "2023", "2022", "2021", "2020")),
                "시즌 목록 조회 성공"
            ));
        }

        JsonNode seasonList = root.path("MRData").path("SeasonTable").path("Seasons");
        List<String> seasons = new ArrayList<>();

        if (seasonList.isArray()) {
            for (JsonNode s : seasonList) {
                seasons.add(s.path("season").asText());
            }
        }

        // 최근 시즌이 앞에 오도록 역순 정렬
        Collections.reverse(seasons);

        // 최근 10개만
        if (seasons.size() > 10) {
            seasons = seasons.subList(0, 10);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("defaultSeason", JolpicaClient.DEFAULT_SEASON);
        result.put("seasons", seasons);

        return ResponseEntity.ok(ResponseData.success(result, "시즌 목록 조회 성공"));
    }
}

package com.f1gg.backend.constructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.f1gg.backend.api.JolpicaClient;
import com.f1gg.backend.api.TranslationService;
import com.f1gg.backend.common.ResponseData;
import com.f1gg.backend.driver.DriverService;
import com.f1gg.backend.driver.DriverResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConstructorService {

    private final JolpicaClient jolpicaClient;
    private final TranslationService translationService;
    private final DriverService driverService;

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

    // 팀 한글명 매핑
    private final Map<String, String> nameKrMap = Map.of(
        "Red Bull", "레드불 레이싱",
        "McLaren", "맥라렌",
        "Ferrari", "페라리",
        "Mercedes", "메르세데스",
        "Aston Martin", "애스턴 마틴",
        "RB F1 Team", "RB F1 팀",
        "Haas F1 Team", "하스 F1 팀",
        "Williams", "윌리엄스",
        "Alpine F1 Team", "알파인 F1 팀",
        "Kick Sauber", "킥 자우버"
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

    // 팀 목록 조회
    public List<ConstructorResponse> getStandings(String season) {
        String targetSeason = (season == null || season.isBlank()) ? JolpicaClient.DEFAULT_SEASON : season;
        JsonNode root = jolpicaClient.getConstructorStandings(targetSeason);
        List<ConstructorResponse> result = new ArrayList<>();

        if (root == null || !root.has("MRData")) return result;

        JsonNode standingsLists = root.path("MRData").path("StandingsTable").path("StandingsLists");
        if (!standingsLists.isArray() || standingsLists.isEmpty()) return result;

        JsonNode standingsList = standingsLists.get(0).path("ConstructorStandings");

        // 드라이버 목록 가져오기 (팀별 드라이버 매핑용)
        List<DriverResponse> drivers = driverService.getStandings(targetSeason);
        Map<String, List<DriverResponse>> driversByTeam = drivers.stream()
                .collect(Collectors.groupingBy(DriverResponse::getTeam));

        if (standingsList.isArray()) {
            for (JsonNode node : standingsList) {
                JsonNode constructor = node.path("Constructor");
                String name = constructor.path("name").asText();

                List<ConstructorResponse.DriverSummary> teamDrivers = driversByTeam
                        .getOrDefault(name, List.of())
                        .stream()
                        .map(d -> ConstructorResponse.DriverSummary.builder()
                                .code(d.getCode())
                                .nameKr(d.getNameKr())
                                .nameEn(d.getNameEn())
                                .number(d.getNumber())
                                .build())
                        .collect(Collectors.toList());

                result.add(ConstructorResponse.builder()
                        .rank(node.path("position").asInt())
                        .constructorId(constructor.path("constructorId").asText())
                        .name(name)
                        .nameKr(nameKrMap.getOrDefault(name, name))
                        .nationality(constructor.path("nationality").asText())
                        .color(colorMap.getOrDefault(name, "#333333"))
                        .points(node.path("points").asText())
                        .wins(node.path("wins").asText())
                        .drivers(teamDrivers)
                        .build());
            }
        }
        return result;
    }

    // 팀 상세 조회
    public ResponseEntity<ResponseData<?>> getConstructorDetail(String id, String season) {
        String targetSeason = (season == null || season.isBlank()) ? JolpicaClient.DEFAULT_SEASON : season;

        // 1. 해당 시즌 순위에서 해당 팀 찾기
        List<ConstructorResponse> standings = getStandings(targetSeason);
        Optional<ConstructorResponse> constructorOpt = standings.stream()
                .filter(c -> c.getConstructorId().equalsIgnoreCase(id))
                .findFirst();

        if (constructorOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseData.notFound("컨스트럭터를 찾을 수 없습니다: " + id));
        }

        ConstructorResponse constructor = constructorOpt.get();

        // 2. 시즌 결과 조회
        JsonNode resultsRoot = jolpicaClient.getConstructorSeasonResults(targetSeason, id);
        List<ConstructorDetailResponse.RaceResult> seasonResults = parseSeasonResults(resultsRoot);

        // 3. 커리어 통계 (Jolpica API 제한으로 현재 시즌 데이터만 사용)
        ConstructorDetailResponse.CareerStats careerStats = ConstructorDetailResponse.CareerStats.builder()
                .championships(0)
                .wins(Integer.parseInt(constructor.getWins()))
                .seasons(1)
                .build();

        // 4. 드라이버 상세 정보
        List<DriverResponse> allDrivers = driverService.getStandings(targetSeason);
        List<ConstructorDetailResponse.DriverInfo> driverInfos = allDrivers.stream()
                .filter(d -> d.getTeam().equals(constructor.getName()))
                .map(d -> ConstructorDetailResponse.DriverInfo.builder()
                        .driverId(d.getDriverId())
                        .code(d.getCode())
                        .number(d.getNumber())
                        .nameKr(d.getNameKr())
                        .nameEn(d.getNameEn())
                        .nationality(d.getNationality())
                        .rank(d.getRank())
                        .points(d.getPoints())
                        .build())
                .collect(Collectors.toList());

        // 5. 응답 조립
        ConstructorDetailResponse response = ConstructorDetailResponse.builder()
                .constructorId(constructor.getConstructorId())
                .name(constructor.getName())
                .nameKr(constructor.getNameKr())
                .nationality(constructor.getNationality())
                .color(constructor.getColor())
                .currentRank(constructor.getRank())
                .currentPoints(constructor.getPoints())
                .currentWins(constructor.getWins())
                .careerStats(careerStats)
                .drivers(driverInfos)
                .seasonResults(seasonResults)
                .build();

        return ResponseEntity.ok(ResponseData.success(response, "컨스트럭터 상세 조회 성공"));
    }

    private List<ConstructorDetailResponse.RaceResult> parseSeasonResults(JsonNode root) {
        List<ConstructorDetailResponse.RaceResult> results = new ArrayList<>();
        if (root == null || !root.has("MRData")) return results;

        JsonNode races = root.path("MRData").path("RaceTable").path("Races");
        if (!races.isArray()) return results;

        for (JsonNode race : races) {
            String raceName = race.path("raceName").asText();
            JsonNode raceResults = race.path("Results");

            List<ConstructorDetailResponse.DriverResult> driverResults = new ArrayList<>();
            int totalPoints = 0;

            if (raceResults.isArray()) {
                for (JsonNode r : raceResults) {
                    int points = (int) Double.parseDouble(r.path("points").asText("0"));
                    totalPoints += points;

                    driverResults.add(ConstructorDetailResponse.DriverResult.builder()
                            .code(r.path("Driver").path("code").asText())
                            .position(r.path("position").asInt())
                            .points(r.path("points").asText())
                            .build());
                }
            }

            results.add(ConstructorDetailResponse.RaceResult.builder()
                    .round(race.path("round").asInt())
                    .raceName(raceName)
                    .raceNameKr(raceNameKrMap.getOrDefault(raceName, raceName))
                    .date(race.path("date").asText())
                    .points(String.valueOf(totalPoints))
                    .driverResults(driverResults)
                    .build());
        }
        return results;
    }

    private ConstructorDetailResponse.CareerStats parseCareerStats(JsonNode root) {
        if (root == null || !root.has("MRData")) {
            return ConstructorDetailResponse.CareerStats.builder()
                    .championships(0).wins(0).seasons(0).build();
        }

        JsonNode standingsList = root.path("MRData").path("StandingsTable").path("StandingsLists");
        if (!standingsList.isArray() || standingsList.isEmpty()) {
            return ConstructorDetailResponse.CareerStats.builder()
                    .championships(0).wins(0).seasons(0).build();
        }

        int championships = 0;
        int totalWins = 0;
        int seasons = standingsList.size();

        for (JsonNode season : standingsList) {
            JsonNode standing = season.path("ConstructorStandings").get(0);
            if (standing != null) {
                if (standing.path("position").asInt() == 1) championships++;
                totalWins += standing.path("wins").asInt();
            }
        }

        return ConstructorDetailResponse.CareerStats.builder()
                .championships(championships)
                .wins(totalWins)
                .seasons(seasons)
                .build();
    }
}

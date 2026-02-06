package com.f1gg.backend.driver;

import com.fasterxml.jackson.databind.JsonNode;
import com.f1gg.backend.api.JolpicaClient;
import com.f1gg.backend.api.TranslationService;
import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final JolpicaClient jolpicaClient;
    private final TranslationService translationService;

    // 이름 캐시
    private final Map<String, String> nameCache = new HashMap<>(Map.of(
        "Max Verstappen", "막스 베르스타펜",
        "Lewis Hamilton", "루이스 해밀턴",
        "Fernando Alonso", "페르난도 알론소"
    ));

    // 팀 컬러 매핑
    private final Map<String, String> colorPatch = Map.of(
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

    public List<DriverResponse> getStandings() {
        JsonNode root = jolpicaClient.getCurrentDriverStandings();
        List<DriverResponse> result = new ArrayList<>();
        
        // 데이터 안전 장치
        if (root == null || !root.has("MRData")) return result;

        JsonNode driverList = root.path("MRData").path("StandingsTable").path("StandingsLists").get(0).path("DriverStandings");

        if (driverList.isArray()) {
            for (JsonNode node : driverList) {
                // 1. 드라이버 노드 접근
                JsonNode driverNode = node.path("Driver");
                JsonNode constructorNode = node.path("Constructors").get(0);

                // 2. 기본 정보 추출
                String givenName = driverNode.path("givenName").asText();
                String familyName = driverNode.path("familyName").asText();
                String fullName = givenName + " " + familyName;
                String teamName = constructorNode.path("name").asText();

                // 3. 자동 번역
                String koreanName;
                if (nameCache.containsKey(fullName)) {
                    koreanName = nameCache.get(fullName);
                } else {
                    koreanName = translationService.translateToKorean(fullName);
                    nameCache.put(fullName, koreanName);
                }

                // 4. DTO에 꽉 채워 넣기
                result.add(DriverResponse.builder()
                        .rank(node.path("position").asInt())
                        .points(node.path("points").asText())
                        .wins(node.path("wins").asText()) // 시즌 우승 횟수
                        
                        .driverId(driverNode.path("driverId").asText()) // 예: max_verstappen
                        .code(driverNode.path("code").asText())         // 예: VER
                        .number(driverNode.path("permanentNumber").asText()) // 예: 1
                        
                        .nameEn(fullName)
                        .nameKr(koreanName)
                        .nationality(driverNode.path("nationality").asText()) // 예: Dutch
                        .dob(driverNode.path("dateOfBirth").asText())         // 예: 1997-09-30
                        
                        .team(teamName)
                        .teamColor(colorPatch.getOrDefault(teamName, "#333333"))
                        .build());
            }
        }
        return result;
    }

    public ResponseEntity<ResponseData<?>> getDriverDetail(String code) {
        // 1. 현재 시즌 드라이버 목록에서 해당 code의 드라이버 찾기
        List<DriverResponse> standings = getStandings();
        Optional<DriverResponse> driverOpt = standings.stream()
                .filter(d -> d.getCode().equalsIgnoreCase(code))
                .findFirst();

        if (driverOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseData.notFound("드라이버를 찾을 수 없습니다: " + code));
        }

        DriverResponse driver = driverOpt.get();
        String driverId = driver.getDriverId();

        // 2. 시즌 경기 결과 조회
        JsonNode resultsRoot = jolpicaClient.getDriverSeasonResults(driverId);
        List<DriverDetailResponse.RaceResult> seasonResults = parseSeasonResults(resultsRoot);

        // 3. 커리어 통계 조회
        JsonNode careerRoot = jolpicaClient.getDriverCareerStats(driverId);
        DriverDetailResponse.CareerStats careerStats = parseCareerStats(careerRoot);

        // 4. 응답 조립
        DriverDetailResponse response = DriverDetailResponse.builder()
                .driverId(driverId)
                .code(driver.getCode())
                .number(driver.getNumber())
                .nameKr(driver.getNameKr())
                .nameEn(driver.getNameEn())
                .nationality(driver.getNationality())
                .dob(driver.getDob())
                .team(driver.getTeam())
                .teamColor(driver.getTeamColor())
                .currentRank(driver.getRank())
                .currentPoints(driver.getPoints())
                .currentWins(driver.getWins())
                .careerStats(careerStats)
                .seasonResults(seasonResults)
                .build();

        return ResponseEntity.ok(ResponseData.success(response, "드라이버 상세 조회 성공"));
    }

    // 시즌 경기 결과 파싱
    private List<DriverDetailResponse.RaceResult> parseSeasonResults(JsonNode root) {
        List<DriverDetailResponse.RaceResult> results = new ArrayList<>();
        if (root == null || !root.has("MRData")) return results;

        JsonNode races = root.path("MRData").path("RaceTable").path("Races");
        if (!races.isArray()) return results;

        // 그랑프리 이름 한글 매핑
        Map<String, String> raceNameKr = Map.ofEntries(
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

        for (JsonNode race : races) {
            JsonNode result = race.path("Results").get(0);
            if (result == null) continue;

            String raceName = race.path("raceName").asText();
            results.add(DriverDetailResponse.RaceResult.builder()
                    .round(race.path("round").asInt())
                    .raceName(raceName)
                    .raceNameKr(raceNameKr.getOrDefault(raceName, raceName))
                    .date(race.path("date").asText())
                    .position(result.path("position").asInt())
                    .points(result.path("points").asText())
                    .grid(result.path("grid").asInt())
                    .status(result.path("status").asText())
                    .build());
        }
        return results;
    }

    // 커리어 통계 파싱
    private DriverDetailResponse.CareerStats parseCareerStats(JsonNode root) {
        if (root == null || !root.has("MRData")) {
            return DriverDetailResponse.CareerStats.builder()
                    .championships(0).wins(0).podiums(0).poles(0).entries(0).seasons(0)
                    .build();
        }

        JsonNode standingsList = root.path("MRData").path("StandingsTable").path("StandingsLists");
        if (!standingsList.isArray() || standingsList.isEmpty()) {
            return DriverDetailResponse.CareerStats.builder()
                    .championships(0).wins(0).podiums(0).poles(0).entries(0).seasons(0)
                    .build();
        }

        int championships = 0;
        int totalWins = 0;
        int seasons = standingsList.size();

        for (JsonNode season : standingsList) {
            JsonNode driverStanding = season.path("DriverStandings").get(0);
            if (driverStanding != null) {
                int position = driverStanding.path("position").asInt();
                int wins = driverStanding.path("wins").asInt();

                if (position == 1) championships++;
                totalWins += wins;
            }
        }

        return DriverDetailResponse.CareerStats.builder()
                .championships(championships)
                .wins(totalWins)
                .podiums(0)  // API에서 직접 제공 안함
                .poles(0)    // API에서 직접 제공 안함
                .entries(0)  // 별도 계산 필요
                .seasons(seasons)
                .build();
    }
}
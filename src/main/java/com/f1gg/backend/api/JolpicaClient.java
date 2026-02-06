package com.f1gg.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // 이거 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class JolpicaClient {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 기본 시즌: 가장 최근 완료된 시즌
    public static final String DEFAULT_SEASON = "2025";

    // ========== Driver API ==========

    public JsonNode getDriverStandings(String season) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + "/driverStandings.json");
    }

    public JsonNode getDriverSeasonResults(String season, String driverId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + "/drivers/" + driverId + "/results.json");
    }

    public JsonNode getDriverCareerStats(String driverId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/drivers/" + driverId + "/driverStandings.json");
    }

    // ========== Constructor API ==========

    public JsonNode getConstructorStandings(String season) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + "/constructorStandings.json");
    }

    public JsonNode getConstructorSeasonResults(String season, String constructorId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + "/constructors/" + constructorId + "/results.json");
    }

    public JsonNode getConstructorCareerStats(String constructorId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/constructors/" + constructorId + "/constructorStandings.json");
    }

    // ========== Schedule API ==========

    public JsonNode getSeasonSchedule(String season) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + ".json");
    }

    public JsonNode getRaceResults(String season, int round) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/" + season + "/" + round + "/results.json");
    }

    // ========== 사용 가능한 시즌 목록 ==========

    public JsonNode getAvailableSeasons() {
        return fetchJson("https://api.jolpi.ca/ergast/f1/seasons.json?limit=100");
    }

    // 공통 fetch 메서드
    private JsonNode fetchJson(String url) {
        String response = restClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패: " + url, e);
        }
    }
}
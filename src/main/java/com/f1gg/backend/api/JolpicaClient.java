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
    // JSON 변환을 도와주는 도구 생성
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    public JsonNode getCurrentDriverStandings() {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current/driverStandings.json");
    }

    // 특정 드라이버의 현재 시즌 결과
    public JsonNode getDriverSeasonResults(String driverId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current/drivers/" + driverId + "/results.json");
    }

    // 특정 드라이버의 커리어 통계 (전체 시즌)
    public JsonNode getDriverCareerStats(String driverId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/drivers/" + driverId + "/driverStandings.json");
    }

    // ========== Constructor API ==========

    // 현재 시즌 컨스트럭터 순위
    public JsonNode getCurrentConstructorStandings() {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current/constructorStandings.json");
    }

    // 특정 컨스트럭터의 현재 시즌 결과
    public JsonNode getConstructorSeasonResults(String constructorId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current/constructors/" + constructorId + "/results.json");
    }

    // 특정 컨스트럭터의 커리어 통계
    public JsonNode getConstructorCareerStats(String constructorId) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/constructors/" + constructorId + "/constructorStandings.json");
    }

    // ========== Schedule API ==========

    // 현재 시즌 일정
    public JsonNode getCurrentSeasonSchedule() {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current.json");
    }

    // 특정 라운드 결과
    public JsonNode getRaceResults(int round) {
        return fetchJson("https://api.jolpi.ca/ergast/f1/current/" + round + "/results.json");
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
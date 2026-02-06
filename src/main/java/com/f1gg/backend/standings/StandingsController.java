package com.f1gg.backend.standings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/standings")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    // 드라이버 순위
    @GetMapping("/drivers")
    public ResponseEntity<ResponseData<StandingsResponse.DriverStandings>> getDriverStandings() {
        StandingsResponse.DriverStandings standings = standingsService.getDriverStandings();
        return ResponseEntity.ok(ResponseData.success(standings, "드라이버 순위 조회 성공"));
    }

    // 컨스트럭터 순위
    @GetMapping("/constructors")
    public ResponseEntity<ResponseData<StandingsResponse.ConstructorStandings>> getConstructorStandings() {
        StandingsResponse.ConstructorStandings standings = standingsService.getConstructorStandings();
        return ResponseEntity.ok(ResponseData.success(standings, "컨스트럭터 순위 조회 성공"));
    }
}

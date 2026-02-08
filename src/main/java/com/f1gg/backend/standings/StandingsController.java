package com.f1gg.backend.standings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1gg.backend.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Standings", description = "순위 API")
@RestController
@RequestMapping("/api/v1/standings")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    @Operation(summary = "드라이버 순위 조회", description = "시즌별 드라이버 챔피언십 순위를 조회합니다.")
    @GetMapping("/drivers")
    public ResponseEntity<ResponseData<StandingsResponse.DriverStandings>> getDriverStandings(
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        StandingsResponse.DriverStandings standings = standingsService.getDriverStandings(season);
        return ResponseEntity.ok(ResponseData.success(standings, "드라이버 순위 조회 성공"));
    }

    @Operation(summary = "컨스트럭터 순위 조회", description = "시즌별 컨스트럭터 챔피언십 순위를 조회합니다.")
    @GetMapping("/constructors")
    public ResponseEntity<ResponseData<StandingsResponse.ConstructorStandings>> getConstructorStandings(
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        StandingsResponse.ConstructorStandings standings = standingsService.getConstructorStandings(season);
        return ResponseEntity.ok(ResponseData.success(standings, "컨스트럭터 순위 조회 성공"));
    }
}

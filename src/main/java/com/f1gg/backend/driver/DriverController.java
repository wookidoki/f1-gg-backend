package com.f1gg.backend.driver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1gg.backend.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Driver", description = "드라이버 API")
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "드라이버 목록 조회", description = "시즌별 드라이버 목록 및 순위 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseData<List<DriverResponse>>> getDrivers(
            @Parameter(description = "시즌 연도 (예: 2025)", example = "2025")
            @RequestParam(required = false) String season) {
        List<DriverResponse> drivers = driverService.getStandings(season);
        return ResponseEntity.ok(ResponseData.success(drivers, "드라이버 목록 조회 성공"));
    }

    @Operation(summary = "드라이버 상세 조회", description = "드라이버 코드로 상세 정보 및 시즌 결과를 조회합니다.")
    @GetMapping("/{code}")
    public ResponseEntity<ResponseData<?>> getDriverDetail(
            @Parameter(description = "드라이버 코드 (예: VER, HAM)", example = "VER")
            @PathVariable String code,
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        return driverService.getDriverDetail(code, season);
    }
}

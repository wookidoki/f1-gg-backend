package com.f1gg.backend.driver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<ResponseData<List<DriverResponse>>> getDrivers(
            @RequestParam(required = false) String season) {
        List<DriverResponse> drivers = driverService.getStandings(season);
        return ResponseEntity.ok(ResponseData.success(drivers, "드라이버 목록 조회 성공"));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ResponseData<?>> getDriverDetail(
            @PathVariable String code,
            @RequestParam(required = false) String season) {
        return driverService.getDriverDetail(code, season);
    }
}

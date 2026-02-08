package com.f1gg.backend.schedule;

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

@Tag(name = "Schedule", description = "레이스 일정 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "시즌 일정 조회", description = "시즌 전체 레이스 일정을 조회합니다.")
    @GetMapping("/schedule")
    public ResponseEntity<ResponseData<ScheduleResponse>> getSchedule(
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        ScheduleResponse schedule = scheduleService.getSchedule(season);
        return ResponseEntity.ok(ResponseData.success(schedule, "시즌 일정 조회 성공"));
    }

    @Operation(summary = "다음 레이스 조회", description = "다음 예정된 레이스 정보를 조회합니다.")
    @GetMapping("/schedule/next")
    public ResponseEntity<ResponseData<?>> getNextRace(
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        return scheduleService.getNextRace(season);
    }

    @Operation(summary = "레이스 결과 조회", description = "특정 라운드의 레이스 결과를 조회합니다.")
    @GetMapping("/races/{round}/results")
    public ResponseEntity<ResponseData<?>> getRaceResult(
            @Parameter(description = "라운드 번호", example = "1")
            @PathVariable int round,
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        return scheduleService.getRaceResult(season, round);
    }

    @Operation(summary = "시즌 목록 조회", description = "사용 가능한 시즌 연도 목록을 조회합니다.")
    @GetMapping("/seasons")
    public ResponseEntity<ResponseData<?>> getSeasons() {
        return scheduleService.getAvailableSeasons();
    }
}

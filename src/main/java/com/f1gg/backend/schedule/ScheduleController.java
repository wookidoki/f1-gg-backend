package com.f1gg.backend.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.f1gg.backend.common.ResponseData;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 시즌 전체 일정
    @GetMapping("/schedule")
    public ResponseEntity<ResponseData<ScheduleResponse>> getSchedule() {
        ScheduleResponse schedule = scheduleService.getSchedule();
        return ResponseEntity.ok(ResponseData.success(schedule, "시즌 일정 조회 성공"));
    }

    // 다음 경기
    @GetMapping("/schedule/next")
    public ResponseEntity<ResponseData<?>> getNextRace() {
        return scheduleService.getNextRace();
    }

    // 특정 라운드 결과
    @GetMapping("/races/{round}/results")
    public ResponseEntity<ResponseData<?>> getRaceResult(@PathVariable int round) {
        return scheduleService.getRaceResult(round);
    }
}

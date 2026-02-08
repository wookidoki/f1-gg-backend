package com.f1gg.backend.constructor;

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

@Tag(name = "Constructor", description = "컨스트럭터(팀) API")
@RestController
@RequestMapping("/api/v1/constructors")
@RequiredArgsConstructor
public class ConstructorController {

    private final ConstructorService constructorService;

    @Operation(summary = "컨스트럭터 목록 조회", description = "시즌별 컨스트럭터 목록 및 순위 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseData<List<ConstructorResponse>>> getConstructors(
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        List<ConstructorResponse> constructors = constructorService.getStandings(season);
        return ResponseEntity.ok(ResponseData.success(constructors, "컨스트럭터 목록 조회 성공"));
    }

    @Operation(summary = "컨스트럭터 상세 조회", description = "컨스트럭터 ID로 상세 정보 및 시즌 결과를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<?>> getConstructorDetail(
            @Parameter(description = "컨스트럭터 ID (예: mclaren, red_bull)", example = "mclaren")
            @PathVariable String id,
            @Parameter(description = "시즌 연도", example = "2025")
            @RequestParam(required = false) String season) {
        return constructorService.getConstructorDetail(id, season);
    }
}

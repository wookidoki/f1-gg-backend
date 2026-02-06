package com.f1gg.backend.constructor;

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
@RequestMapping("/api/v1/constructors")
@RequiredArgsConstructor
public class ConstructorController {

    private final ConstructorService constructorService;

    // 팀 목록 조회
    @GetMapping
    public ResponseEntity<ResponseData<List<ConstructorResponse>>> getConstructors(
            @RequestParam(required = false) String season) {
        List<ConstructorResponse> constructors = constructorService.getStandings(season);
        return ResponseEntity.ok(ResponseData.success(constructors, "컨스트럭터 목록 조회 성공"));
    }

    // 팀 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<?>> getConstructorDetail(
            @PathVariable String id,
            @RequestParam(required = false) String season) {
        return constructorService.getConstructorDetail(id, season);
    }
}

package com.f1gg.backend.driver;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponse {
    // 1. 순위 정보
    private int rank;           // 현재 순위
    private String points;      // 현재 포인트
    private String wins;        // 이번 시즌 우승 횟수 (NEW!)

    // 2. 드라이버 기본 정보
    private String driverId;    // 고유 ID (예: max_verstappen) -> 상세페이지 링크용 (NEW!)
    private String code;        // 약어 (예: VER, HAM) -> TV 중계 스타일 (NEW!)
    private String number;      // 등번호 (예: 1, 44) (NEW!)
    
    // 3. 이름 및 국적
    private String nameKr;      // 한글 이름
    private String nameEn;      // 영어 이름
    private String nationality; // 국적 (예: Dutch) -> 국기 표시용 (NEW!)
    private String dob;         // 생년월일 (YYYY-MM-DD) -> 나이 계산용 (NEW!)

    // 4. 팀 정보
    private String team;        // 팀 이름
    private String teamColor;   // 팀 컬러
}
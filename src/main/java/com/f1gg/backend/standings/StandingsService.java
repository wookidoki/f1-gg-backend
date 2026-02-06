package com.f1gg.backend.standings;

import com.f1gg.backend.driver.DriverResponse;
import com.f1gg.backend.driver.DriverService;
import com.f1gg.backend.constructor.ConstructorResponse;
import com.f1gg.backend.constructor.ConstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final DriverService driverService;
    private final ConstructorService constructorService;

    // 팀 컬러 매핑
    private final Map<String, String> colorMap = Map.of(
        "Red Bull", "#0600EF",
        "McLaren", "#FF8000",
        "Ferrari", "#C00000",
        "Mercedes", "#00D2BE",
        "Aston Martin", "#006F62",
        "RB F1 Team", "#6692FF",
        "Haas F1 Team", "#B6BABD",
        "Williams", "#64C4FF",
        "Alpine F1 Team", "#0090FF",
        "Kick Sauber", "#52E252"
    );

    // 팀 한글명 매핑
    private final Map<String, String> nameKrMap = Map.of(
        "Red Bull", "레드불 레이싱",
        "McLaren", "맥라렌",
        "Ferrari", "페라리",
        "Mercedes", "메르세데스",
        "Aston Martin", "애스턴 마틴",
        "RB F1 Team", "RB F1 팀",
        "Haas F1 Team", "하스 F1 팀",
        "Williams", "윌리엄스",
        "Alpine F1 Team", "알파인 F1 팀",
        "Kick Sauber", "킥 자우버"
    );

    // 드라이버 순위 조회
    public StandingsResponse.DriverStandings getDriverStandings() {
        List<DriverResponse> drivers = driverService.getStandings();

        List<StandingsResponse.DriverEntry> standings = drivers.stream()
                .map(d -> StandingsResponse.DriverEntry.builder()
                        .position(d.getRank())
                        .points(d.getPoints())
                        .wins(d.getWins())
                        .driver(StandingsResponse.Driver.builder()
                                .driverId(d.getDriverId())
                                .code(d.getCode())
                                .number(d.getNumber())
                                .nameKr(d.getNameKr())
                                .nameEn(d.getNameEn())
                                .nationality(d.getNationality())
                                .build())
                        .constructor(StandingsResponse.Constructor.builder()
                                .constructorId(d.getTeam().toLowerCase().replace(" ", "_"))
                                .name(d.getTeam())
                                .nameKr(nameKrMap.getOrDefault(d.getTeam(), d.getTeam()))
                                .color(d.getTeamColor())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return StandingsResponse.DriverStandings.builder()
                .season("2024")
                .round(drivers.isEmpty() ? 0 : 24) // 현재 라운드
                .standings(standings)
                .build();
    }

    // 컨스트럭터 순위 조회
    public StandingsResponse.ConstructorStandings getConstructorStandings() {
        List<ConstructorResponse> constructors = constructorService.getStandings();

        List<StandingsResponse.ConstructorEntry> standings = constructors.stream()
                .map(c -> StandingsResponse.ConstructorEntry.builder()
                        .position(c.getRank())
                        .points(c.getPoints())
                        .wins(c.getWins())
                        .constructor(StandingsResponse.Constructor.builder()
                                .constructorId(c.getConstructorId())
                                .name(c.getName())
                                .nameKr(c.getNameKr())
                                .color(c.getColor())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return StandingsResponse.ConstructorStandings.builder()
                .season("2024")
                .round(constructors.isEmpty() ? 0 : 24)
                .standings(standings)
                .build();
    }
}

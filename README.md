# F1.GG

F1 시즌 일정, 경기 결과, 드라이버·팀 순위를 한곳에서 보는 웹 서비스입니다.

- 백엔드: https://github.com/wookidoki/f1-gg-backend
- 프론트엔드: https://github.com/wookidoki/f1-gg-frontend

## 기능

- 시즌별 경기 일정과 결과, 다음 경기 카운트다운
- 드라이버·컨스트럭터 순위와 상세 정보
- 순위 변화 차트
- 회원가입, 로그인, 즐겨찾기

## 기술

| 구분 | 내용 |
|---|---|
| 백엔드 | Java, Spring Boot, Spring Security + JWT, Swagger |
| 프론트엔드 | React, Vite |
| 데이터 | Jolpica F1 API |

## 실행

백엔드

```bash
./gradlew bootRun
```

프론트엔드

```bash
npm install
npm run dev
```

# 부하 테스트 결과 기록

## 테스트 환경

| 항목        | 내용                                                    |
| ----------- | ------------------------------------------------------- |
| 테스트 도구 | JMeter 5.6.3                                            |
| 서버        | Spring Boot — localhost:8080                            |
| 모니터링    | Grafana + Prometheus                                    |
| 아키텍처    | Hexagonal (Presentation / Application / Infrastructure) |

> JMeter HTML 리포트 생성
>
> ```
> jmeter -g /Users/jae/results/폴더명/파일명.jtl -o /Users/jae/results/폴더명/html_report
> ```

---

## 시나리오 A — Baseline (10 Users)

> 목적: 정상 부하 상태에서의 기준값 수립. 이후 모든 시나리오 비교의 기준선.
> 실행: 3회 반복 → 중앙값 기록 (1회 / 2회 / 3회)

### A-1. 엔드포인트별 응답 시간 (JMeter)

| 엔드포인트                |         p50 (ms)         |         p95 (ms)         |     p95-p50 (ms)      |     에러율 (%)     |        RPS         |
| ------------------------- | :----------------------: | :----------------------: | :-------------------: | :----------------: | :----------------: |
| GET /api/hospital/nearby  |  89.00 / 84.00 / 90.00   |  99.00 / 96.00 /100.95   | 10.00 / 12.00 / 10.95 | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| GET /api/hospital/search  |  22.00 / 26.00 / 25.00   |  42.95 / 38.90 / 42.00   | 20.95 / 12.90 / 17.00 | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| GET /api/hospital/{code}  |  13.00 / 10.00 / 11.00   |  26.95 / 17.00 / 17.00   |  13.95 / 7.00 / 6.00  | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| GET /api/schedule         |  28.00 / 25.00 / 29.00   |  40.95 / 34.00 / 35.00   |  12.95 / 9.00 / 6.00  | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| POST /api/schedule        |  17.00 / 19.00 / 19.00   |  26.00 / 25.00 / 24.00   |  9.00 / 6.00 / 5.00   | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| DELETE /api/schedule/{id} |  17.00 / 14.00 / 16.00   |  26.00 / 22.00 / 22.00   |  9.00 / 8.00 / 6.00   | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| POST /api/member/login    | 140.00 / 171.00 / 172.00 | 161.00 / 185.95 / 191.00 | 21.00 / 14.95 / 19.00 | 0.00 / 0.00 / 0.00 | 1.65 / 1.64 / 1.64 |
| **전체 합산**             |                          |                          |                       |                    |                    |

> p95-p50: 200ms 이상이면 꼬리 지연(tail latency) 불안정 신호

### A-2. 서버 리소스 (Grafana — Saturation 섹션)

| 항목                  |        측정값         |  단위   | Grafana 패널                    |
| --------------------- | :-------------------: | :-----: | ------------------------------- |
| CPU % 피크            |  4.32 / 4.74 / 4.82   |    %    | Process CPU Usage               |
| Heap 피크 (MB)        | 133.6 / 107.4 / 140.5 |   MB    | JVM Memory → heap (÷ 1,048,576) |
| Non-Heap 피크 (MB)    | 155.6 / 151.1 / 152.0 |   MB    | JVM Memory → nonheap            |
| JVM Live Threads 피크 |     44 / 44 / 44      | threads | JVM Live Threads                |

### A-3. HTTP 섹션 (Jmeter)

| 항목                    |          측정값          | 단위  | Grafana 패널                    |
| ----------------------- | :----------------------: | :---: | ------------------------------- |
| 전체 RPS                |  12.68 / 12.78 / 12.75   | req/s | 전체 HTTP 요청량 (RPS)          |
| HTTP p95 응답 시간 (ms) | 143.00 / 172.00 / 188.00 |  ms   | HTTP 응답 지연 시간 (p95)       |
| HTTP p50 응답 시간 (ms) |  22.00 / 22.00 / 22.00   |  ms   | HTTP 요청 응답 시간 (p50)       |
| p95 - p50 편차 (ms)     | 121.00 / 120.00 / 166.00 |  ms   | HTTP 요청 응답 시간 (p95 - p50) |

---

## 시나리오 B-1 — 단계별 스트레스 (Knee Point 탐지)

> 목적: 부하 증가 시 성능이 급격히 무너지는 임계 사용자 수(Knee Point) 탐지
> 엔드포인트: 혼합 부하 (hospital_search 40% / hospital_nearby 30% / hospital_detail 15% / schedule CRUD 10% / login-logout 매 iteration)
> 실행: 10 → 30 → 50 → 70 → 100 Users 순차 실행 (serialize=true, 각 300s)

### B-1-1. 단계별 JMeter 지표

| 동시 사용자 | p50 (ms) | p95 (ms) | p95-p50 (ms) | RPS | 에러율 (%) | Knee Point |
| :---------: | :------: | :------: | :----------: | :-: | :--------: | :--------: |
|  10 Users   |          |          |              |     |            |            |
|  30 Users   |          |          |              |     |            |            |
|  50 Users   |          |          |              |     |            |            |
|  70 Users   |          |          |              |     |            |            |
|  100 Users  |          |          |              |     |            |            |

> Knee Point: 이전 단계 p95 대비 2배 이상 급증하는 지점에 O 표시

### B-1-2. 단계별 서버 리소스 (Grafana — Saturation 섹션, 각 구간 피크값)

| 동시 사용자 | CPU % 피크 | Heap 피크 (MB) | Live Threads 피크 |
| :---------: | :--------: | :------------: | :---------------: |
|  10 Users   |            |                |                   |
|  30 Users   |            |                |                   |
|  50 Users   |            |                |                   |
|  70 Users   |            |                |                   |
|  100 Users  |            |                |                   |

### B-1-3. 도메인별 RPS 비율 (Grafana — Domain 섹션, 50 Users 구간 기준)

| 도메인   | RPS | 비율 (%) | Grafana 패널           |
| -------- | :-: | :------: | ---------------------- |
| hospital |     |          | 도메인별 요청 수 (RPS) |
| schedule |     |          | 도메인별 요청 수 (RPS) |
| member   |     |          | 도메인별 요청 수 (RPS) |

---

## 시나리오 B-2 — 레이어별 병목 분석 (50 Users)

> 목적: Hexagonal 아키텍처 레이어(Presentation / Application / Infrastructure) 중 병목 레이어 탐지
> 엔드포인트: B-1과 동일한 혼합 부하, 단독 실행 (50 Users, 600s)
> 출처: Grafana Layer 섹션

### B-2-1. 레이어별 응답 시간

| 엔드포인트               | Presentation p95 (ms) | Application 평균 (ms) | Infrastructure 평균 (ms) | 병목 레이어 |
| ------------------------ | :-------------------: | :-------------------: | :----------------------: | :---------: |
| GET /api/hospital/search |                       |                       |                          |             |
| GET /api/hospital/nearby |                       |                       |                          |             |
| GET /api/hospital/{code} |                       |                       |                          |             |

> Grafana 패널 매핑
>
> - Presentation p95: Layer → Presentation p95 응답 시간
> - Application 평균: Layer → Application 평균 비즈니스 처리 시간
> - Infrastructure 평균: Layer → Infrastructure 외부 의존성 평균 응답 시간

> 병목 판단 기준
>
> - Infrastructure > 100ms → DB 쿼리 병목 (인덱스 추가 또는 쿼리 최적화)
> - Application > 50ms → 비즈니스 로직 병목 (알고리즘 개선, /nearby Haversine 계산 예상)
> - Presentation > Application + Infrastructure 합 → AOP / JSON 직렬화 / Security Filter 체인

### B-2-2. Presentation 실패율 (Grafana — Layer 섹션)

| 항목                      | 측정값 | Grafana 패널                     |
| ------------------------- | :----: | -------------------------------- |
| Presentation 실패율 (%)   |        | Presentation 실패율 (stat)       |
| Hospital 레이어 요청 비율 |        | Hospital 도메인 레이어별 요청 수 |

---

## 시나리오 B-3 — 집중 부하 (엔드포인트 한계 탐색)

> 목적: 특정 엔드포인트에 집중 부하를 가했을 때 성능 한계 및 서버 영향 측정
> B3-Hospital: GET /api/hospital/search (파라미터 없음) + GET /api/hospital/nearby — 50 Users, 300s
> B3-Search: GET /api/hospital/search 4종 — 30 Users, 300s
> 실행: B3-Hospital → B3-Search 순차 실행 (serialize=true)

| 시나리오    | 엔드포인트                               | p50 (ms) | p95 (ms) | 에러율 (%) | 응답 크기 | Heap 피크 (MB) |
| :---------- | :--------------------------------------- | :------: | :------: | :--------: | :-------: | :------------: |
| B3-Hospital | GET /api/hospital/search (파라미터 없음) |          |          |            |    MB     |                |
| B3-Hospital | GET /api/hospital/nearby                 |          |          |            |    KB     |                |
| B3-Search   | GET /api/hospital/search                 |          |          |            |    KB     |                |

> 응답 크기: JMeter JTL bytes 컬럼 평균값 (÷ 1024 = KB, ÷ 1048576 = MB)
> GET /api/hospital/search (파라미터 없음)의 응답 크기와 Heap 증가 상관관계 확인

---

## 시나리오 C — 인증 집중 부하

> 목적: 동시 로그인 폭증 시 HikariCP 커넥션 경쟁 및 JWT 발급 성능 측정
> 구성: login → Gaussian Wait(2000~5000ms) → logout 반복
> 실행: 10 → 30 → 50 Users 순차 실행 (serialize=true, 각 300s, ramp-up 비례)
> 확인 포인트: HikariCP pool=10 대비 최대 50 users → 5배 초과 시 대기 발생 여부

### C-1. 단계별 응답 시간 (JMeter)

| 항목             | 10 Users | 30 Users | 50 Users |
| ---------------- | :------: | :------: | :------: |
| login p50 (ms)   |          |          |          |
| login p95 (ms)   |          |          |          |
| logout p95 (ms)  |          |          |          |
| RPS (login 기준) |          |          |          |
| 에러율 (%)       |          |          |          |

### C-2. 서버 리소스 (Grafana — Saturation 섹션, 각 구간 피크값)

| 항목               | 10 Users | 30 Users | 50 Users | Grafana 패널                   |
| ------------------ | :------: | :------: | :------: | ------------------------------ |
| CPU % 피크         |          |          |          | Process CPU Usage              |
| Heap 피크 (MB)     |          |          |          | JVM Memory → heap              |
| Live Threads 피크  |          |          |          | JVM Live Threads               |
| GC Pause 발생 여부 |          |          |          | JVM Memory 수직 하강 구간 유무 |

> HikariCP 커넥션 지표 (`hikaricp_connections_active`, `hikaricp_connections_pending`)는
> 현재 대시보드에 패널 미포함 — Prometheus `/actuator/prometheus` 직접 조회 또는 대시보드 패널 추가 필요

---

## 시나리오 D — 외부 API (OpenAI 증상 체크)

> 목적: OpenAI API 호출 응답 시간 및 실패율 측정
> 설정: 3 users × 5 loops = 15회 (OpenAI 비용 제어)
> 실패 감지: HTTP 200이지만 data:null 응답 → Response Assertion으로 감지

### D-1. 응답 시간 (JMeter)

| 항목                  | 측정값 |
| --------------------- | :----: |
| 평균 응답 시간 (ms)   |        |
| p95 응답 시간 (ms)    |        |
| 최소 응답 시간 (ms)   |        |
| 최대 응답 시간 (ms)   |        |
| 실패 건수 / 전체 건수 |  / 15  |
| 실패율 (%)            |        |

### D-2. 실패 패턴 분석

| 항목                   | 내용                                      |
| ---------------------- | ----------------------------------------- |
| 실패 발생 loop         |                                           |
| 실패 발생 thread       |                                           |
| 실패 응답 크기 (bytes) |                                           |
| 실패 응답 내용         | View Results Tree → Response Body 탭 확인 |
| 실패 원인 추정         |                                           |

### D-3. Grafana External API 섹션

| 항목                             | 측정값 | Grafana 패널                          |
| -------------------------------- | :----: | ------------------------------------- |
| OpenAI p95 응답 시간 (ms)        |        | External API p95 응답 시간 (시스템별) |
| OpenAI 실패율 (%)                |        | OpenAI 실패율 (stat)                  |
| External API 전체 평균 응답 (ms) |        | External API 전체 평균 응답 시간      |

> OpenAI 응답 시간 기준 (실측 기반)
>
> - 정상: < 20s / 경고: 20s~28s / 위험: > 28s 또는 data:null 반환

---

## Spring Batch Job 성능

> 출처: Grafana Batch 섹션
> 패널: Job별 실행 시간 P95 / Spring Batch Job 성공·실패 횟수 / Job별 평균 실행 시간

| Job 이름 | 실행 횟수 | 성공 | 실패 | P95 실행 시간 (s) | 평균 실행 시간 (s) |
| -------- | :-------: | :--: | :--: | :---------------: | :----------------: |
|          |           |      |      |                   |                    |
|          |           |      |      |                   |                    |

> 기준: P95 정상 < 30s / 경고 30s~2m / 위험 > 2m
> 전체 배치 Job 평균: Grafana → 전체 배치 Job 평균 실행 시간 패널

---

## 리팩토링 전/후 비교

> 아래 표는 코드 개선 후 동일 시나리오·동일 조건으로 재실행하여 효과를 수치로 기록

### 개선 항목별 p95 변화

| 개선 항목                                  | 개선 전 p95 | 개선 후 p95 | 개선율 | 기준 시나리오 |
| ------------------------------------------ | :---------: | :---------: | :----: | :-----------: |
| GET /api/hospital/search 페이지네이션 적용 |     ms      |     ms      |   %    | B-3 Hospital  |
| GET /api/hospital/nearby 결과 수 제한      |     ms      |     ms      |   %    | B-3 Hospital  |
| hospital_search 쿼리 최적화 (인덱스)       |     ms      |     ms      |   %    |  B-3 Search   |
| OpenAI 예외처리 개선 (null → 502)          |  실패율 %   |  실패율 %   |   %    |       D       |

### 개선 항목별 서버 리소스 변화 (50 Users 기준)

| 개선 항목                                  | 개선 전 Heap (MB) | 개선 후 Heap (MB) | 개선 전 CPU % | 개선 후 CPU % |
| ------------------------------------------ | :---------------: | :---------------: | :-----------: | :-----------: |
| GET /api/hospital/search 페이지네이션 적용 |                   |                   |               |               |
| GET /api/hospital/nearby 결과 수 제한      |                   |                   |               |               |

---

## Grafana 패널별 체크포인트

### HTTP 섹션

| 패널명                          |     정상      |     경고      |                위험                 | 기록 항목           |
| ------------------------------- | :-----------: | :-----------: | :---------------------------------: | ------------------- |
| 전체 HTTP 요청량 (RPS)          | 목표 RPS 도달 |       -       | JMeter 대비 현저히 낮음 (서버 거절) | 시나리오별 최대 RPS |
| HTTP 응답 지연 시간 (p95)       |    < 500ms    |  500ms ~ 1s   |                > 1s                 | 단계별 Users 각각   |
| HTTP 요청 응답 시간 (p50)       |    < 200ms    | 200ms ~ 500ms |               > 500ms               | 시나리오별          |
| HTTP 요청 응답 시간 (p95 - p50) |    < 200ms    | 200ms ~ 500ms |      > 500ms (꼬리 지연 심각)       | 편차가 크면 불안정  |

### Saturation 섹션

| 패널명            |       정상        |    경고     |           위험           | 기록 항목            |
| ----------------- | :---------------: | :---------: | :----------------------: | -------------------- |
| Process CPU Usage |       < 60%       |  60% ~ 80%  |          > 80%           | Users별 구간 피크값  |
| JVM Memory (heap) | 부하 후 정상 복귀 | 완만한 증가 | 지속 증가 (GC 누수 의심) | 부하 전 / 중 / 후 MB |
| JVM Live Threads  |       < 200       |  200 ~ 400  |     > 400 또는 급증      | 단계별 피크값        |

### Layer 섹션 (Hexagonal 레이어 분석)

| 패널명                                    |  정상   |     경고      |  위험   | 기록 항목            |
| ----------------------------------------- | :-----: | :-----------: | :-----: | -------------------- |
| Presentation p95 응답 시간                | < 500ms |  500ms ~ 1s   |  > 1s   | 엔드포인트별         |
| Application 평균 비즈니스 처리 시간       | < 50ms  | 50ms ~ 200ms  | > 200ms | 레이어 처리 오버헤드 |
| Infrastructure 외부 의존성 평균 응답 시간 | < 100ms | 100ms ~ 300ms | > 300ms | DB/외부 의존성 성능  |
| Application 처리 지연 (Presentation 대비) | < 50ms  | 50ms ~ 200ms  | > 200ms | AOP 오버헤드 참고    |
| Presentation 실패율                       |  < 1%   |    1% ~ 5%    |  > 5%   | 시나리오별           |

> 병목 레이어 해석
>
> ```
> [케이스 1] Infrastructure 평균 >> Application 평균
>   → DB 쿼리 병목. 인덱스 추가 또는 쿼리 최적화 필요
>
> [케이스 2] Application 평균 >> Infrastructure 평균
>   → 비즈니스 로직 병목. 알고리즘 개선 필요
>   → GET /api/hospital/nearby 에서 이 패턴 예상 (Haversine 전체 계산)
>
> [케이스 3] Presentation p95 >> (Application + Infrastructure) 합
>   → AOP 오버헤드, JSON 직렬화, Security Filter 체인 확인
> ```

### Domain 섹션

| 패널명                           | 기록 항목                                              |
| -------------------------------- | ------------------------------------------------------ |
| 도메인별 요청 수 (RPS)           | hospital / schedule / member RPS 비율                  |
| Hospital 도메인 레이어별 요청 수 | presentation / application / infrastructure 레이어 RPS |

### External API 섹션

| 패널명                                | 정상  |   경고    | 위험  | 기록 항목      |
| ------------------------------------- | :---: | :-------: | :---: | -------------- |
| External API p95 응답 시간 (시스템별) | < 20s | 20s ~ 28s | > 28s | ms 단위 실측값 |
| External API 전체 평균 응답 시간      | < 15s | 15s ~ 25s | > 25s | ms 단위 실측값 |
| OpenAI 실패율 (stat)                  | < 1%  |  1% ~ 5%  | > 5%  | % 실측값       |
| FCM 실패율                            | < 1%  |  1% ~ 5%  | > 5%  | % 실측값       |
| External API 요청 수 (시스템별)       |   -   |     -     |   -   | RPS 실측값     |

### Batch 섹션

| 패널명                             | 정상  |   경고    |        위험        | 기록 항목           |
| ---------------------------------- | :---: | :-------: | :----------------: | ------------------- |
| Job별 실행 시간 P95                | < 30s | 30s ~ 2m  | > 2m 또는 타임아웃 | Job별 P95 실행 시간 |
| Spring Batch Job 성공 / 실패 횟수  | > 99% | 95% ~ 99% |       < 95%        | 성공·실패 비율      |
| 전체 배치 Job 평균 실행 시간       | < 10s | 10s ~ 60s |       > 60s        | 시나리오별 평균     |
| Job별 평균 실행 시간               | < 10s | 10s ~ 60s |       > 60s        | Job별 평균          |
| Spring Batch Job 실행 횟수 (Job별) |   -   |     -     |         -          | Job별 실행 횟수     |

# Scenario A Baseline — 부하 테스트 트러블슈팅 보고서

> **테스트 환경**: `http://localhost:8080` / 동시 사용자 10명 / 30회 반복 / 총 2,400건
> **최종 결과**: 4차례 반복을 통해 성공률 0% → 100% 달성

---

## 트러블슈팅 개요

| 이슈 | 현상                                      | 원인                                     | 해결                                    |
| ---- | ----------------------------------------- | ---------------------------------------- | --------------------------------------- |
| 1    | 특정 API 요청이 서버에 아예 도달하지 않음 | JMeter 경로 변수 문법 오류               | `{변수}` → `${변수}`                    |
| 2    | login 포함 전 API 400 응답                | Content-Type 헤더 공백 + 설정 중복       | 헤더 공백 제거 및 중복 설정 정리        |
| 3    | 인증 필요 API 전부 401 응답               | 서버 인증 구조 미파악 (Cookie vs Bearer) | 백엔드 소스 분석 후 추출·전달 방식 교체 |
| 4    | schedule(POST)만 401 지속                 | Request Body 스펙 불일치                 | Body 필드 구조 수정                     |

---

## Issue #1 — 특정 요청이 서버에 도달하지 않음

### 현상

테스트 실행 직후 `hospital_{hospitalCode}`와 `schedule_{scheduleId}` 두 엔드포인트에서
응답 시간 **0ms**, 수신 바이트 **0** 이라는 비정상적인 결과가 관측됨.

```
[JTL 로그]
label: hospital_{hospitalCode}
elapsed: 0ms
bytes: 1185 (JMeter 내부 오류 메시지)
responseCode: Non HTTP response code: java.net.URISyntaxException
responseMessage: Illegal character in path at index 35:
                 http://localhost:8080/api/hospital/{hospitalCode}
```

### 원인 분석

JMeter가 URL을 그대로 서버로 전송하려 하자 Java URI 파서가 `{`, `}` 문자를
**불법 문자(Illegal Character)** 로 판단하여 요청 생성 단계에서 에러를 반환

원인은 JMX 설정에서 경로 변수를 JMeter 변수 문법이 아닌 일반 중괄호로 작성

```
[JMX 설정 - 잘못됨]
/api/hospital/{hospitalCode}    ← JMeter가 변수로 인식하지 못함

[JMX 설정 - 올바름]
/api/hospital/${hospitalCode}   ← JMeter 변수 참조 문법
```

### 해결

JMX 내 두 HTTP Sampler의 경로를 수정.

```
/api/hospital/{hospitalCode}   →   /api/hospital/${hospitalCode}
/api/schedule/{scheduleId}     →   /api/schedule/${scheduleId}
```

### 검증

수정 후 두 엔드포인트의 `URISyntaxException`이 사라지고 정상적으로 서버에 요청이 전달됨.

---

## Issue #2 — 전 엔드포인트 HTTP 400 응답

### 현상

1차 테스트에서 `URISyntaxException`이 발생한 2개를 제외한 나머지 6개 엔드포인트가
모두 **HTTP 400** 을 반환  
 → login 실패를 의미

```
[JTL 로그 - login]
responseCode: 400
failureMessage: Test failed: code expected to contain /200/
```

응답 시간이 1~5ms로 극히 짧음 → 서버가 요청 내용을 처리하기도 전에 거부

### 원인 분석

**원인 1 — Content-Type 헤더 trailing space**

JMX의 HTTP Header Manager를 확인한 결과, 헤더 이름과 값에 후행 공백이 포함되어 있었음.

```xml
<!-- JMX 원본 -->
<stringProp name="Header.name">Content-Type </stringProp>   ← 공백 포함
<stringProp name="Header.value">application/json </stringProp>  ← 공백 포함
```

HTTP 스펙상 헤더 이름의 후행 공백은 허용되지 않으며, 일부 서버는 이를 유효하지 않은 헤더로 처리함.
서버가 `Content-Type`을 인식하지 못해 Request Body를 파싱하지 못하고 400을 반환한 것으로 판단.

**원인 2 — 설정 요소 중복 정의**

JMX 구조를 분석한 결과, `CSVDataSet`, `HTTP Request Defaults`, `CookieManager`가
Thread Group 내부와 외부에 **각각 2번씩** 정의되어 있었음.

```
ThreadGroup
 ├── CSVDataSet       ← 내부 정의
 ├── HTTP Defaults    ← 내부 정의
 └── CookieManager   ← 내부 정의

[TestPlan 레벨]
├── CSVDataSet       ← 중복! (외부)
├── HTTP Defaults    ← 중복! (외부)
└── CookieManager   ← 중복! (외부)
```

중복 설정이 서로 충돌하여 예상치 못한 동작을 유발할 가능성이 있었음.

### 해결

```
1. Content-Type 헤더 수정
   "Content-Type " → "Content-Type"
   "application/json " → "application/json"

2. Thread Group 외부에 중복 정의된 CSVDataSet, HTTP Defaults, CookieManager 제거

3. CSVDataSet의 ignoreFirstLine을 false → true 로 변경 (헤더행 스킵)
```

### 검증

수정 후 login이 HTTP 200으로 성공. hospital 계열 API(인증 불필요)도 전부 200 확인

---

## Issue #3 — 인증 필요 API 전부 HTTP 401 응답

### 현상

Issue #1, #2 해결 후 2차 테스트를 실행한 결과, login을 포함한 공개 API는 정상화되었으나
인증이 필요한 4개 엔드포인트가 전부 **HTTP 401** 을 반환

```
schedule(GET)   → 401  (응답 2~3ms, 즉시 거부)
schedule(POST)  → 401  (응답 2~3ms, 즉시 거부)
schedule_1      → 401  (응답 2~3ms, 즉시 거부)
Logout          → 401  (응답 2~3ms, 즉시 거부)
```

응답 시간 2~3ms → 서버가 비즈니스 로직을 실행하기 전에 인증 필터에서 즉시 차단하는 상황

### 원인 분석

**1단계 — 초기 가설: accessToken JSON 경로 불일치**

login 응답 Body에서 토큰을 추출하는 `JSONPostProcessor`의 경로가 실제 응답 구조와 다를 것으로 가정

```
설정된 경로: $.data.accessToken
가설: 실제 응답 JSON에 해당 경로가 없어 기본값 "INVALID_TOKEN" 사용 중
```

**2단계 — 백엔드 소스 직접 분석**

`MemberController.java`, `JwtFilter.java`, `CookieUtil.java`, `SecurityConfig.java` 를 순서대로 분석.

```java
// MemberController.java - login()
AuthTokenResult tokenResult = authenticationService.authenticateAndIssueTokens(...);
setTokenCookies(request, response, tokenResult.accessToken(), tokenResult.refreshToken());
return ApiResponse.ok(mapper.dtoToResponse(tokenResult.member()));  // Body에는 회원 정보만 포함
```

```java
// CookieUtil.java - addCookie()
cookie.setSecure(true);   // ← HTTPS 전용 쿠키로 발급
```

```java
// JwtFilter.java - doFilterInternal()
String accessToken = extractTokenFromCookies(request, "access_token");  // ← 쿠키에서만 읽음
```

**3단계 — 실제 원인 확정**

소스 분석으로 두 가지 구조적 문제를 확인:

| 문제             | 내용                                                                                                                             |
| ---------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| 토큰 위치 오인   | accessToken은 Response Body가 아닌 **Set-Cookie 응답 헤더**로 전달됨                                                             |
| 인증 방식 오인   | 서버는 `Authorization: Bearer` 헤더를 읽지 않고 **`access_token` 쿠키**에서만 토큰 읽음                                          |
| Secure 쿠키 문제 | `cookie.setSecure(true)` → JMeter가 `http://` 프로토콜에서 Secure 쿠키를 자동 전송하지 않음 → CookieManager가 있어도 토큰 미전달 |

```
[기존 설정 - 잘못됨]
토큰 추출: JSONPostProcessor → Response Body ($.data.accessToken)
토큰 전달: Authorization: Bearer ${accessToken}

[실제 서버 동작]
토큰 전달: 로그인 응답 Set-Cookie 헤더 (access_token=eyJ...)
토큰 검증: 요청의 Cookie 헤더에서 access_token 값 추출
```

### 해결

Secure 쿠키를 `http://` 환경에서 우회하는 수동 주입 방식으로 변경.

**변경 1 — 토큰 추출: JSONPostProcessor → RegexExtractor**

```xml
<!-- 수정 전: Response Body에서 JSON 경로로 추출 (동작 안 함) -->
<JSONPostProcessor>
  <stringProp name="JSONPostProcessor.jsonPathExprs">$.data.accessToken</stringProp>
</JSONPostProcessor>

<!-- 수정 후: Response Headers에서 정규식으로 Set-Cookie 값 추출 -->
<RegexExtractor>
  <stringProp name="RegexExtractor.useHeaders">true</stringProp>
  <stringProp name="RegexExtractor.regex">access_token=([^;]+)</stringProp>
  <stringProp name="RegexExtractor.refname">accessToken</stringProp>
</RegexExtractor>
```

**변경 2 — 토큰 전달: Authorization 헤더 → Cookie 헤더 수동 주입**

```xml
<!-- 수정 전: 서버가 읽지 않는 Authorization 헤더 -->
<Header.name>Authorization</Header.name>
<Header.value>Bearer ${accessToken}</Header.value>

<!-- 수정 후: JwtFilter가 실제로 읽는 Cookie 헤더로 수동 주입 -->
<Header.name>Cookie</Header.name>
<Header.value>access_token=${accessToken}</Header.value>
```

### 검증

수정 후 3차 테스트에서 schedule(GET), schedule_1(DELETE), Logout이 모두 200으로 전환됨.
응답 시간도 2~3ms(즉시 거부) → 15~16ms(실제 비즈니스 로직 처리)로 정상화됨.

---

## Issue #4 — schedule(POST)만 지속적으로 401 응답

### 현상

Issue #3 해결 후 3차 테스트에서 schedule(POST)만 유일하게 **HTTP 401** 을 유지.
동일한 Cookie 헤더를 사용하는 다른 인증 필요 API는 모두 정상.

```
schedule(GET)       → 200 ✅  (동일 Cookie 헤더, 인증 성공)
schedule(POST)      → 401 ❌  (동일 Cookie 헤더, 인증 실패)  ← 이상
schedule_1(DELETE)  → 200 ✅  (동일 Cookie 헤더, 인증 성공)
Logout              → 200 ✅  (동일 Cookie 헤더, 인증 성공)
```

10개 스레드 × 30회 반복 = **300건 전부 동일하게 실패** → 간헐적 오류가 아닌 구조적 문제.

### 원인 분석

토큰 자체 문제가 아님을 GET·DELETE 성공으로 확인 → Request Body 구조 문제로 범위를 좁힘.

기존 JMX의 schedule(POST) Body 필드 구조가 실제 API 스펙과 불일치한 것을 확인

### 해결

schedule(POST)의 Request Body를 실제 서버 API 스펙에 맞는 필드 구조로 수정

### 검증

4차 테스트에서 schedule(POST) 300건 전부 HTTP 200으로 전환됨
최종 전체 성공률 100% 달성

---

## 최종 테스트 결과 (4차)

| 엔드포인트          | 메서드 | 성공률 | 평균  | p90   | p95   | p99   |
| ------------------- | ------ | ------ | ----- | ----- | ----- | ----- |
| login               | POST   | 100%   | 142ms | 153ms | 154ms | 165ms |
| hospital_search     | GET    | 100%   | 27ms  | 43ms  | 45ms  | 48ms  |
| hospital_H000001    | GET    | 100%   | 12ms  | 16ms  | 18ms  | 27ms  |
| hospital_nearby     | GET    | 100%   | 82ms  | 90ms  | 93ms  | 101ms |
| schedule(GET)       | GET    | 100%   | 16ms  | 21ms  | 22ms  | 28ms  |
| schedule(POST)      | POST   | 100%   | 19ms  | 23ms  | 25ms  | 27ms  |
| schedule_1 (DELETE) | DELETE | 100%   | 16ms  | 22ms  | 24ms  | 26ms  |
| Logout              | POST   | 100%   | 16ms  | 21ms  | 23ms  | 27ms  |

**전체 처리량: 12.87 req/s / 에러율: 0%**

---

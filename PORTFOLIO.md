# HosPin

> 병원 탐색 및 추천 플랫폼

---

## 프로젝트 개요

| 항목           | 내용                                           |
| -------------- | ---------------------------------------------- |
| **프로젝트명** | HosPin                                         |
| **유형**       | 개인 프로젝트                                  |
| **주제**       | 사용자 위치 기반 병원 탐색 및 진료 예약 서비스 |

### 핵심 기능

- 위치 기반 주변 병원 탐색 (3km 이내)
- 진료 일정 등록 / 수정 / 삭제 및 알림
- OpenAI 기반 증상 분석 챗봇
- Firebase 푸시 알림
- 이메일 인증 및 아이디 찾기

---

## 기술 스택

### Backend

| 분류             | 기술                                      | 버전   |
| ---------------- | ----------------------------------------- | ------ |
| **언어**         | Java                                      | 21     |
| **프레임워크**   | Spring Boot                               | 3.5.4  |
| **ORM**          | Spring Data JPA / Hibernate               | -      |
| **데이터베이스** | MariaDB                                   | -      |
| **캐시 / 세션**  | Redis (Lettuce)                           | -      |
| **인증**         | Spring Security + JWT                     | 0.12.3 |
| **AI 연동**      | Spring AI (OpenAI ChatClient)             | 1.1.2  |
| **푸시 알림**    | Firebase Admin SDK                        | 9.7.0  |
| **이메일**       | Spring Mail (Gmail SMTP) + Thymeleaf      | -      |
| **배치**         | Spring Batch                              | -      |
| **모니터링**     | Micrometer + Prometheus + Spring Actuator | -      |
| **빌드 도구**    | Gradle                                    | -      |
| **유틸리티**     | Lombok, MapStruct, Apache Commons Lang3   | -      |

### Frontend

| 분류               | 기술                   | 버전   |
| ------------------ | ---------------------- | ------ |
| **프레임워크**     | SvelteKit              | 2.22.0 |
| **언어**           | TypeScript             | 5.0.0  |
| **스타일링**       | Tailwind CSS + DaisyUI | 4.0.0  |
| **빌드 도구**      | Vite                   | 7.0.4  |
| **캘린더**         | FullCalendar           | 6.1.19 |
| **지도**           | Naver Maps API         | -      |
| **푸시 알림**      | Firebase SDK           | 12.7.0 |
| **API 클라이언트** | OpenAPI Fetch          | 0.15.0 |
| **배포**           | Vercel                 | -      |

---

## 시스템 아키텍처

### 전체 구조

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (Vercel)                │
│           SvelteKit + TypeScript + Tailwind         │
└───────────────────────┬─────────────────────────────┘
                        │ REST API (JSON)
┌───────────────────────▼─────────────────────────────┐
│                  Backend (Spring Boot)              │
│  ┌─────────────────────────────────────────────────┐│
│  │              Presentation Layer                 ││
│  ├─────────────────────────────────────────────────┤│
│  │              Application Layer                  ││
│  ├─────────────────────────────────────────────────┤│
│  │               Domain Layer                      ││
│  ├─────────────────────────────────────────────────┤│
│  │             Infrastructure Layer                ││
│  └────────────────────────────────────────────────┘│
└──────┬──────────────┬──────────────┬────────────────┘
       │              │              │
  ┌────▼────┐   ┌─────▼─────┐ ┌────▼────────┐
  │ MariaDB │   │   Redis   │ │  외부 서비스  │
  └─────────┘   └───────────┘ │ Firebase    │
                               │ OpenAI      │
                               │ Gmail SMTP  │
                               └─────────────┘
```

### 백엔드 패키지 구조

```
com.hp.hospin/
├── member/          # 회원 인증 / 관리
├── hospital/        # 병원 데이터 / 검색 / 지도
├── schedule/        # 진료 일정 CRUD
├── notification/    # FCM 푸시 알림
├── batch/           # 배치 처리
└── global/          # 공통 (Configurations, Exception, ...)
```

---

## 아키텍처 설계 원칙

### 1. 헥사고날 아키텍처 (Ports & Adapters)

도메인 로직과 외부 의존성을 인터페이스(Port)로 분리하여 유연한 구조를 구현했습니다.

```java
// Domain Layer - Port 인터페이스 정의
public interface SymptomAnalyzePort {
    String analyze(String symptomDescription);
}

// Infrastructure Layer - Adapter 구현
public class OpenAiSymptomAnalyzeAdapter implements SymptomAnalyzePort {
    private final ChatClient chatClient;

    @Override
    public String analyze(String symptomDescription) {
        // OpenAI API 호출
    }
}
```

**효과:** 예를 들어 OpenAI 대신 다른 AI 서비스로 교체 시 Adapter만 변경하면 되므로 도메인 로직 불변

### 2. 표준화된 API 응답 구조

모든 API 응답에 일관된 포맷 적용:

```json
{
  "statusCode": 200,
  "message": "OK",
  "resultType": "SUCCESS",
  "errorCode": null,
  "data": {}
}
```

`resultType`: `SUCCESS` / `VALIDATION_EXCEPTION` / `CUSTOM_EXCEPTION` / `IMPOSSIBLE`

### 3. 도메인별 커스텀 예외 처리

```
ErrorCode (Enum)
    └── CustomException
            └── GlobalExceptionHandler (@ControllerAdvice)
```

각 도메인마다 에러 코드를 정의하고, `@ControllerAdvice`로 전역 처리.

---

## 주요 구현 사항

### JWT 인증 흐름

```
클라이언트 로그인 요청
    → Spring Security 인증
    → JwtTokenProvider.generateToken()
    → Access Token + Refresh Token 발급 (Cookie)
    → 이후 요청마다 JwtFilter에서 검증
    → CustomUserDetailsService로 유저 정보 로드
```

- **알고리즘:** HS256
- **세션:** STATELESS (JWT 기반)
- **CSRF:** 비활성화 (stateless API)
- **Refresh Token:** Redis에 저장하여 무효화(로그아웃) 처리

### 위치 기반 병원 탐색

- Naver Maps API로 사용자 현재 위치 좌표 획득
- 백엔드에서 위도/경도를 기반으로 반경 3km 이내 병원 필터링
- 병원 목록을 거리순으로 정렬하여 반환

### OpenAI 증상 분석

```
사용자 증상 입력
    → Spring AI ChatClient 호출
    → OpenAiSymptomAnalyzeAdapter (Port 구현)
    → 응답 파싱 후 추천 진료과 반환
    → Micrometer로 API 호출 시간 측정
```

### Spring Batch 병원 데이터 처리

공공데이터 CSV 파일을 일괄 처리하여 DB에 적재:

```
ItemReader (CSV) → ItemProcessor (변환/검증) → ItemWriter (JPA 저장)
```

- Chunk Size: 1,000건
- 처리 대상: 병원 기본정보, 진료과, 등급 데이터
- JobListener로 배치 시작/종료 모니터링

### 모니터링

```
@Monitored (커스텀 AOP 어노테이션)
    → Micrometer Timer 측정
    → Prometheus 수집 (/actuator/prometheus)
    → Grafana 시각화
```

외부 API(OpenAI) 호출에 대한 성능 지표도 별도 수집.

---

## API 설계

| 메서드 | 엔드포인트                         | 설명               | 인증   |
| ------ | ---------------------------------- | ------------------ | ------ |
| POST   | `/api/member/join`                 | 회원가입           | 불필요 |
| POST   | `/api/member/login`                | 로그인             | 불필요 |
| POST   | `/api/member/logout`               | 로그아웃           | 필요   |
| GET    | `/api/member`                      | 내 정보 조회       | 필요   |
| GET    | `/api/member/findId`               | 아이디 찾기        | 불필요 |
| GET    | `/api/hospital`                    | 병원 목록          | 불필요 |
| GET    | `/api/hospital/{code}`             | 병원 상세          | 필요   |
| GET    | `/api/hospital/nearby`             | 주변 병원 탐색     | 불필요 |
| GET    | `/api/hospital/search`             | 병원 검색 (페이징) | 불필요 |
| GET    | `/api/schedule`                    | 내 일정 목록       | 필요   |
| POST   | `/api/schedule`                    | 일정 등록          | 필요   |
| PUT    | `/api/schedule/{id}`               | 일정 수정          | 필요   |
| DELETE | `/api/schedule/{id}`               | 일정 삭제          | 필요   |
| GET    | `/api/schedule/getClosestSchedule` | 다가오는 일정      | 필요   |
| POST   | `/api/symptomcheck`                | 증상 분석          | 불필요 |

---

## 기술적 도전 및 해결

### 1. 도메인 모델과 JPA 엔티티 분리

- **문제:** JPA 어노테이션이 도메인 모델을 오염시키는 문제
- **해결:** `Hospital` (도메인 객체) 와 `JpaHospitalEntity` (영속성 객체) 를 별도로 정의하고 MapStruct로 변환

### 2. 외부 API 의존성 관리

- **문제:** OpenAI API 장애 시 서비스 전체에 영향
- **해결:** Adapter 패턴으로 격리, 추후 Fallback 전략 적용 가능한 구조로 설계

### 3. 배치 성능

- **문제:** 대용량 병원 공공데이터 처리 시 메모리 부족
- **해결:** Spring Batch Chunk 처리(1,000건 단위)로 메모리 사용량 제어

### 4. 토큰 무효화 (로그아웃)

- **문제:** JWT는 stateless 특성상 서버에서 즉시 무효화 불가
- **해결:** Redis에 Refresh Token 저장, 로그아웃 시 Redis에서 삭제하여 재발급 차단

---

## 환경 설정 전략

| 프로파일 | 용도      | DDL 전략   |
| -------- | --------- | ---------- |
| `dev`    | 로컬 개발 | `update`   |
| `prod`   | 운영      | `validate` |

민감 정보(DB 비밀번호, API 키 등)는 `application-secret.yml`(git 제외)로 분리.

---

## 프로젝트에서 적용한 핵심 역량

| 역량              | 적용 내용                                                   |
| ----------------- | ----------------------------------------------------------- |
| **백엔드 설계**   | 헥사고날 아키텍처, 레이어드 아키텍처, DDD 원칙 적용         |
| **인증/보안**     | Spring Security + JWT, Stateless 세션, Redis 기반 토큰 관리 |
| **AI 연동**       | Spring AI를 활용한 OpenAI ChatClient 통합                   |
| **성능 모니터링** | Micrometer + Prometheus 커스텀 메트릭, AOP 기반 @Monitored  |
| **배치 처리**     | Spring Batch로 대용량 공공데이터 적재                       |
| **실시간 알림**   | Firebase FCM 푸시 알림 구현                                 |
| **풀스택 개발**   | Spring Boot 백엔드 + SvelteKit 프론트엔드 전체 구현         |
| **RESTful API**   | 표준화된 응답 구조, 커스텀 예외 처리, 페이징 처리           |
| **인프라**        | Redis 캐싱, MariaDB, 환경별 프로파일 분리                   |

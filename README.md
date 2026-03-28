# HosPin

[![hospin.png](https://i.postimg.cc/1zq44y9C/choejong.png)](https://postimg.cc/R3vvD5JK)

> 병원 탐색 및 추천 플랫폼

## [HosPin](https://wwww.hospin.site)

---

## 프로젝트 개요

| 항목           | 내용                                           |
| -------------- | ---------------------------------------------- |
| **프로젝트명** | HosPin                                         |
| **유형**       | 개인 프로젝트                                  |
| **주제**       | 병원 등급을 이용하여 사용자에게 정보 전달 및 사용자 위치 기반 병원 탐색 서비스 |

---

## 성능 향상

### 데이터 읽기

* **부하 테스트 (JMeter — 리팩토링 전 대비)**

  | 항목 | 이전 | 이후 | 개선율 |
  |------|------|------|--------|
  | GET /api/hospital/nearby p95 | 98.65ms | 11.33ms | **88.5% ↑** |
  | POST /api/member/login p95 | 179.32ms | 81.32ms | **54.7% ↑** |
  | HTTP 전체 p95 응답 시간 | 167.67ms | 75.67ms | **54.9% ↑** |
  | GET /api/hospital/search p95 | 26.33ms | 17ms | **35.4% ↑** |
  | Schedule 평균 응답 시간 | 36.9ms | 24.3ms | **34.1% ↑** |
  | 처리량 (RPS, 100 users) | 174.4 req/s | 212.4 req/s | **21.8% ↓** |
  | Heap 메모리 피크 | 277.4MB | 254.1MB | **8.4% ↑** |

* **Spring Batch 성능 개선**

  | Batch Job | 이전 | 이후 | 개선율 |
  |-----------|------|------|--------|
  | loadHospitalDetailJob | 3h 24m | 55s | **99.54% ↑ (218.7배)** |
  | loadHospitalGradeJob | 32m 56s | 36s | **98.14% ↑ (53.8배)** |
  | 전체 | 3시간 57분 | 1분 32초 | **99.35% ↑ (153배)** |
  | 데이터 유실률 | 58.9% | 0.27% | **329,957건 회수** |

총 읽어오는 데이터의 수는 총 **562,205**개의 데이터를 읽어 저장한다.


---

## 기술 스택

### Backend

[![Back.png](https://i.postimg.cc/3Nj1q7hv/seukeulinsyas-2026-03-29-010841.png)](https://postimg.cc/BP628rXS)

### Frontend

[![Front.png](https://i.postimg.cc/yxhjGBKF/seukeulinsyas-2026-03-29-012127.png)](https://postimg.cc/9wMy46rM)

### Deploy

[![Deploy.png](https://i.postimg.cc/1X0BdPS0/seukeulinsyas-2026-03-29-012510.png)](https://postimg.cc/fSLY3GJk)

### Infrastructure

[![Infrastructure.png](https://i.postimg.cc/4yvP0ZGQ/seukeulinsyas-2026-03-29-022907.png)](https://postimg.cc/GT2vBWH4)

### CICD

[![CICD.png](https://i.postimg.cc/j26c1KTQ/seukeulinsyas-2026-03-29-024943.png)](https://postimg.cc/T5wnyzpp)

---

## 시스템 아키텍처

### 전체 구조

#### 레이어드 + 헥사고날 아키택처

```
┌─────────────────────────────────────────────────────┐
│                    Frontend (Vercel)                │
│           SvelteKit + TypeScript + Tailwind         │
└───────────────────────┬─────────────────────────────┘
                        │ REST API
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

레이어드 아키택처에서 각 레이어를 나눠서 구조를 명확하게 만들어 주는 장점은 계층별 독립적으로 수립할 수 있는 장점이 존재한다. 
하지만, Infrastructure 계층은 Domain 계층 아래에 존재한다. 이는 암묵적으로 Infrastructure에 의존하고 있다고 생각한다. 그렇기 때문에 헥사고날 아키택처의 Port/Adapter의 개념을 추가하여 의존성 방향을 역전시켜 Domain이 Infrastructure의 구현체를 숨겨서 모르는 상태를 만들어 주고 싶었다.

결과적으로 레이어드 아키택처의 구조의 명확함과 헥사고날 아키택처의 Domain의 독립성 두 가지 이점을 챙겼다.

각 레이어는 다음 레이어의 구현체를 직접 참조 대신 반환되는 데이터에 집중하도록 구현

```java
public interface SymptomAnalyzePort {
    String analyze(String symptomDescription);
}

public class OpenAiSymptomAnalyzeAdapter implements SymptomAnalyzePort {
    private final ChatClient chatClient;
    @Override
    public String analyze(String symptomDescription) {
        ...
    }
}
```


## MVP

### 위치 기반 병원 탐색

[![위치 기반 병원 탐색.png](https://i.postimg.cc/mkDBP1Jn/wichi-giban-byeong-won-tamsaeg.png)](https://postimg.cc/xJWW780G)

```
사용자의 위도, 경도를 정수로 변환하여 DB에 병원 데이터를 조회
                            |
조회한 데이터를 Haversine 공식으로 필터링 (반경 3km 데이터 필터링)
                            |
                        데이터 반환                            
```

위 같은 최초 구현 상태의 문제점은 최초의 위도, 경도에 대해서 정수로 변환하여 데이터를 조회하기 때문에 반환해야 하는 데이터를 반환하지 못하게 된다.
```
a병원 위치 - (36.9999, y)
사용자 위치 - (37.0001, y)
=> 사용자는 근처 병원인 a병원의 데이터 제외됨
```

**1차 리펙토링**
기존 방법의 문제점을 해결하기 위해 서버에서 위도, 경도 값을 사용하여 정사각형의 바운더리를 만들어서 해당 범위에 해당하는 데이터 조회하고, 다시 서버 내에서 Haversine 공식으로 2차 필터링을 하여 반환하게 수정했다.

**2차 리팩토링**
이번 리팩토링에서는 서버에서 Haversine 공식, 정렬을 하지 않고 DB에서 데이터를 조회할 때 실행하도록 수정하였다.    
추가적으로 병원 데이터를 조회할 때 78,774개에서 데이터를 찾았지만, 위도, 경도에 인덱스를 추가하여 데이터를 조회할 때 일정 데이터에서 조회하도록 변경하였다.

---

* OpenAI 증상 분석

[![OpenAI 증상 분석.png](https://i.postimg.cc/QCthFKwm/Open-AI-jeungsang-bunseog.png)](https://postimg.cc/vgppjTQx)
  
사용자가 입력한 증상을 바탕으로 진료하면 좋은 진료과가 있는 병원을 추천하는 기능이다.

해당 기능은 서버 내 프롬프트에 일정 규격으로 대답을 반환하라고 작성하였다.   
이렇게 반환된 데이터에서 진료과 정보를 추출하여 진료과 코드로 맵핑하여 사용자의 위치와 진료과 코드 정보를 사용하여 데이터를 추출하도록 설계하였다.

---

## ERD

[![ERD.png](https://i.postimg.cc/y6S087ww/seukeulinsyas-2026-03-29-030912.png)](https://postimg.cc/dDwhWcQB)

---

## API 명세서

### 공통 응답 구조

```json
{
  "statusCode": 200,
  "message": "OK",
  "resultType": "SUCCESS",
  "errorCode": null,
  "data": {}
}
```

---

| 메서드 | 엔드포인트 | 설명 | 인증 |
| ------ | ---------- | ---- | ---- |
| `POST` | `/api/member/join` | 회원가입 | X |
| `POST` | `/api/member/login` | 로그인 (Access Token + Refresh Token 발급) | X |
| `POST` | `/api/member/logout` | 로그아웃 (Refresh Token 무효화) | O |
| `GET` | `/api/member` | 내 정보 조회 | O |
| `GET` | `/api/member/findId` | 이메일로 아이디 찾기 | X |
| `GET` | `/api/hospital` | 병원 목록 조회 | X |
| `GET` | `/api/hospital/{code}` | 병원 상세 조회 | O |
| `GET` | `/api/hospital/nearby` | 반경 3km 이내 주변 병원 탐색 (위도/경도 기반) | X |
| `GET` | `/api/hospital/search` | 병원 검색 (키워드, 페이징) | X |
| `GET` | `/api/schedule` | 내 진료 일정 목록 | O |
| `POST` | `/api/schedule` | 진료 일정 등록 | O |
| `PUT` | `/api/schedule/{id}` | 진료 일정 수정 | O |
| `DELETE` | `/api/schedule/{id}` | 진료 일정 삭제 | O |
| `GET` | `/api/schedule/getClosestSchedule` | 가장 가까운 다가오는 일정 조회 | O |
| `POST` | `/api/symptomcheck` | 증상 입력 → OpenAI 분석 → 추천 진료과 반환 | X |

---
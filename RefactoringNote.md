# 문제점 - Batch

#hospin

현재 프로젝트에서 Open API, CSV 데이터를 이용하여 병원 데이터를 수집하고 Spring Batch를 사용하여 데이터 베이스에 적재를 하고 있다.

## 데이터 규모

|     **구분**     | **제공 데이터 수** |
| :--------------: | :----------------: |
|    병원 정보     |       78,774       |
|  병원 상세 정보  |       24,956       |
| 병원 진료과 정보 |      421,540       |
|  병원 등급 정보  |       36,935       |
|     **합계**     |    **562,205**     |

# 문제 상황

- ### 오래걸리는 배치

전반적으로 모든 배치 동작들이 생각보다 오래걸리는 문제점이 있지만, 특정 배치에서만 몇 시간이 넘는 실행 시간을 갖고 있다는 문제점 발견
| **Batch Job** | **실행 시간** |
|:---------------------:|:-------------------:|
| loadHospitalJob | **38s 676ms** |
| loadHospitalDetailJob | **3h 24m 0s 813ms** |
| loadHospitalGradeJob | **32m 56s880ms** |

- ### 데이터 유실
  데이터 유실 또한 많은 양의 데이터가 유실되고 있다는 것을 확인 가능하다.
  | **Batch Job** | 데이터의 수 | Reader | Writer | 유실 |
  |:--------------------------:|:-------:|:-------:|:------:|:-------:|
  | loadHospitalStep | 78,774 | 78,774 | 78,774 | 0 |
  | loadHospitalDetailStep | 24,956 | 24,956 | 23,576 | 1,380 |
  | loadHospitalDepartmentStep | 421,540 | 185,506 | 56,469 | 365,071 |
  | loadHospitalGradeStep | 36,935 | 35,962 | 35,836 | 1,099 |

이 같은 문제점들은 프로젝트의 목적과 운영적인 측면에서 치명적인 문제가 된다는 것을 확인할 수 있다.

---

# 문제 해결 시도

## 문제점 1. 오래걸리는 배치

### 문제 상황

- loadHospitalDetailJob

```java
@Bean(name ="loadHospitalDetailJob" )
public Job loadHospitalDetailJob() {
    return new JobBuilder("loadHospitalDetailJob", jobRepository)
            .start(loadHospitalDetailStep())
            .next(loadHospitalDepartmentStep())
            .listener(jobListener)
            .build();

}

@JobScope
@Bean
public Step loadHospitalDetailStep() {
    return new StepBuilder("loadHospitalDetailStep", jobRepository)
            .<HospitalDetailRegister, JpaHospitalDetailEntity>chunk(1000,ptm)
            .reader(hospitalDetailReader.readerByHospitalDetail())
            .processor(hospitalDetailProcessor)
            .writer(hospitalDetailWriter)
            .build();
}

@JobScope
@Bean
public Step loadHospitalDepartmentStep() {
    return new StepBuilder("loadHospitalDepartmentStep", jobRepository)
            .<HospitalCodeWithDepartments, JpaHospitalDetailEntity>chunk(1000,ptm)
            .reader(groupedHospitalDepartmentReader)
            .processor(hospitalDepartmentProcessor)
            .writer(hospitalDepartmentWriter)
            .build();
}
```

`loadHospitalDetailJob`의 경우에는 내부에 2개의 Step으로 구성되어 있다.
각 Step의 간략한 동작 순서는 아래와 같다.

```
* loadHospitalDetailStep
		Reader
          │
          ▼
   병원 상세 데이터 읽기
          │
          ▼
Hospitals 테이블에서
동일한 ykiho 존재 여부 확인
          │
     ┌────┴────┐
     │         │
     ▼         ▼
   존재함     존재하지 않음
     │         │
     ▼         ▼
Hospitals_Detail   필터링 (skip)
엔티티 생성
     │	Writer
     ▼
데이터베이스 저장
(Hospitals_Detail)
```

```
* loadHospitalDepartmentStep
		Reader
          │
          ▼
   병원 진료과 데이터 읽기
          │
          ▼
Hospitals_Detail 테이블에서
동일한 ykiho 존재 여부 확인
          │
     ┌────┴────┐
     │         │
     ▼         ▼
   존재함     존재하지 않음
     │         │
     ▼         ▼
Hospitals_Detail   필터링 (skip)
엔티티 조회
     │
     ▼
DepartmentCodes 추가
     │	Writer
     ▼
Hospitals_Detail 업데이트 저장
```

### 문제 분석

(loadHospitalDetailStep -> Step 1, loadHospitalDepartmentStep -> Step 2 으로 편하게 부르겠다.)

> 문제 1.
> 해당 구조에서 `Step1`의 processor에서 item마다 Hospitals 테이블에 동일한 ykiho를 갖고 있는지 조회를 하고, 해당 데이터가 존재를 하면 저장한다.
>
> 여기서 각 item의 수 만큼 계속 DB에 쿼리를 날려 확인을 하고 있다는 문제점을 확인할 수 있다.

> 문제2.
> `Step1`에서 저장한 데이터를 `Step2`에서 다시 동일한 ykiho를 꺼내서 수정하고 저장하는 동작을 하고 있다.
>
> 하나의 `JOB`에서 두 Step은 동일 데이터를 2번 저장하고 있는 꼴이 된다.

그렇기 때문에 비효율적인 동작과 **전형적인 Batch N+1 문제**가 발생하여 오래 걸리게 된것이라 판단했다.

추가적으로 `loadHospitalGradeJob`의 경우에도 문제 1과 동일한 동작을 갖고 있다는 것을 확인했다.

### 문제 해결 방안

- #### JobExecutionListener + In-Memory Cache
  Cahce를 사용하여 각 Step에 해당 데이터를 전달하여 비효율적인 동작을 제거 하는 방법으로 수정하면 문제를 개선할 수 있을 것이라 생각했다.

Step1 에게는 **JobExecutionListener 패턴**을 사용하여 해당 Job이 실행될 시점에 `beforeJob`으로 데이터베이스에 존재하는 ykiho 데이터를 모두 긁어오고 Step1에게 전달해주도록 설계 하였다.

```
Job 시작
   ↓
beforeJob()
   ↓
Step 실행
```

전달된 데이터를 사용하여 Processer에서 기존에 N개의 데이터에 대한 DB 조회를 N번을 진행했었다면,
성능 개선으로 DB조회 횟수는 1로 급감하게 되었다.

- #### **In-Memory Cache**

Step1의 Writer에서 DB에 데이터 저장과 동시에 `Map<String, Object>` 구조로 In-Memory에 동시에 저장을 한다. 그리고 메모리에 있는 캐시 데이터를 Step2 Processor에서 해당 데이터를 사용하여 추가적인 쿼리 사용을 막았다.

- 전체적인 동작 순서

```
DB hospital_code
        ↓
JobListener (캐시 preload)

Step1
CSV → Processor → Writer
        ↓
DB 저장 + Cache 저장 (HospitalDetailCache)

Step2
CSV → Processor
        ↓
Cache 조회
        ↓
departmentCodes 업데이트
        ↓
Writer → DB update
```

---

### 결과

- ### 오래걸리는 배치

  |     **Batch Job**     | **기존 실행 시간**  | **현재 실행 시간** |    성늘 향상    |  속도 배율  |
  | :-------------------: | :-----------------: | :----------------: | :-------------: | :---------: |
  |    loadHospitalJob    |    **38s 676ms**    |         -          |        -        |      -      |
  | loadHospitalDetailJob | **3h 24m 0s 813ms** |   **55s 986ms**    | **99.54% 향상** | **218.7배** |
  | loadHospitalGradeJob  |  **32m 56s880ms**   |   **36s 730ms**    | **98.14% 향상** | **53.8배**  |
  |       **전체**        |  **약 3시간 57분**  |  **약 1분 32초**   | **99.35% 향상** | **153.3배** |

  Batch 코드 관련 동작 방식을 `DB 조회 -> 캐시 조회` 으로 변경하였다. 이를 통하여 대규모 CSV, Open API 기반 데이터 적재 환경에서 **Batch N+1 문제와 불필요한 DB 접근을 제거하고, 약 153배 이상의 성능 개선을 달성하였다.**

---

## 문제점 2. 데이터 유실

### 문제 상황

|       **Batch Step**       | **제공 데이터** | **Reader** | **Writer** |  **유실**   |
| :------------------------: | :-------------: | :--------: | :--------: | :---------: |
|   loadHospitalDetailStep   |     24,956      |   24,956   |   23,576   |  **1,380**  |
| loadHospitalDepartmentStep |     421,540     |  185,506   |   56,469   | **365,071** |
|   loadHospitalGradeStep    |     36,935      |   35,962   |   35,836   |  **1,099**  |

---

### 문제 분석

각 Step마다 유실 원인이 달랐다

|          **Step**          | **유실 건** | **원인 요약**                                                           |
| :------------------------: | :---------: | :---------------------------------------------------------------------- |
|   loadHospitalDetailStep   |    1,380    | hospitalS에 없는 ykiho → FK 제약으로 저장 불가 (구조적 유실)            |
| loadHospitalDepartmentStep |   365,071   | ① CSV 비정렬로 그룹핑 오류 ② 재실행 시 캐시 비어있음 ③ Writer 예외 묵인 |
|   loadHospitalGradeStep    |    1,099    | ① numOfRows 부족으로 API 미수신                                         |

---

#### loadHospitalDetailStep

```
hospital_detail.csv
       │
       ▼
  Processor
       │
  HospitalCodeCache 조회
       │
  ┌────┴────┐
  │         │
존재함    존재하지 않음
  │         │
  ▼         ▼
저장      null 반환 (skip)
           → 1,380건 유실
```

hospitals 테이블에 없는 병원코드는 FK 제약으로 저장 자체가 불가능하다. 구조적 유실이므로 해결 불가.

---

#### loadHospitalDepartmentStep

**CSV 비정렬 그룹핑 오류 — Reader**

기존 그룹핑 로직은 연속된 행만 같은 병원으로 묶었다. 원천 데이터가 hospitalCode 기준으로 정렬되어 있지 않아서 같은 병원의 진료과가 파일 전체에 흩어져 있으면 여러 그룹으로 쪼개지게 된다.

```java
// 기존 — 연속 행이 다르면 즉시 그룹 종료
if (!row.getHospitalCode().equals(currentCode)) {
    nextRow = row;
    break; // 비연속 동일 코드 = 별개 그룹으로 취급 → 유실
}
```

```
CSV (비정렬 상태)
A병원 진료과1
B병원 진료과1
A병원 진료과2  ← 연속 아님
      │
      ▼
그룹1: A병원 [진료과1]   ← 진료과2 유실
그룹2: B병원 [진료과1]
그룹3: A병원 [진료과2]   ← 덮어쓰기
```

421,540행이 185,506개 그룹으로 줄어든 이유가 이것이다.

**재실행 시 캐시 비어있음 — Processor**

Step1 Writer는 DB 저장과 동시에 `HospitalDetailCache`에도 데이터를 적재한다. 최초 실행에는 문제없지만 **재실행 시** Step1 데이터가 이미 DB에 있으니 캐시는 빈 채로 Step2가 시작된다.

```java
// 기존 — beforeJob() 없음, afterJob()만 존재
public void afterJob(JobExecution jobExecution) {
    detailCache.clear();
    codeCache.clear();
}
// beforeJob() 누락 → 재실행 시 HospitalDetailCache 항상 비어있음
```

```java
// 기존 — 캐시 미스 시 무조건 null 반환
cache.get(item.getHospitalCode()).orElseThrow(RuntimeException::new);
// 캐시에 없으면 예외 → null 반환 → 진료과 전체 유실
```

**Writer 예외 묵인**

```java
// 기존 — 예외 삼키기
try {
    entityManager.merge(entity);
} catch (Exception e) {
    e.printStackTrace(); // 조용히 넘어감 → Spring Batch는 정상으로 간주
}
```

---

#### loadHospitalGradeStep

**① numOfRows 부족**

```java
// 기존 — 실제 데이터 수(36,935)보다 낮게 설정
Long numOfRows = 35962L; // 973건 미수신
```

---

### 해결 방안

| **문제**                | **해결**                                       |
| :---------------------- | :--------------------------------------------- |
| CSV 비정렬 그룹핑 오류  | 전체 CSV를 LinkedHashMap으로 읽어 완전 그룹핑  |
| 재실행 시 캐시 비어있음 | beforeJob()에서 기존 DB 데이터 캐시 선적재     |
| 캐시 미스 → 무조건 skip | HospitalCodeCache 2차 확인 후 최소 엔티티 생성 |
| Writer 예외 묵인        | try-catch 제거, 예외를 Batch 프레임워크로 전파 |
| numOfRows 부족          | 40,000으로 상향 (실제 수보다 여유 설정)        |

---

### 해결

#### GroupedHospitalDepartmentReader — 전체 그룹핑

```java
// 수정 후 — 데이터를 LinkedHashMap으로 읽어 완전 그룹핑
Map<String, HospitalCodeWithDepartments> grouped = new LinkedHashMap<>();
HospitalDepartmentRow row;
while ((row = csvReader.read()) != null) {
    String code = row.getHospitalCode();
    String name = row.getHospitalName();
    String deptCode = row.getDepartmentCode();
    grouped.computeIfAbsent(code,
            k -> new HospitalCodeWithDepartments(k, name, new ArrayList<>()))
            .getDepartmentCodes().add(deptCode);
}
this.groupedIterator = grouped.values().iterator();
```

#### HospitalDetailJobListener — beforeJob() 추가

```java
// 수정 후 — Job 시작 시 기존 DB 데이터 캐시 선적재
@Override
public void beforeJob(JobExecution jobExecution) {
    hospitalDetailJPARepository.clearAllDepartmentCodes(); // 진료과 출처 일관성 보장
    List<JpaHospitalDetailEntity> existing = hospitalDetailJPARepository.findAll();
    existing.forEach(entity -> detailCache.put(mapper.toDomain(entity)));
    log.info("기존 병원 상세 캐시 preload 완료: {}건", existing.size());
}
```

#### HospitalDepartmentProcessor — 2단계 fallback

```java
// 수정 후 — HospitalDetailCache 미스 시 HospitalCodeCache로 2차 확인
if (fromCache.isPresent()) {
    JpaHospitalDetailEntity entity = fromCache.get();
    entity.setDepartmentCodes(item.getDepartmentCodes());
    return entity;
}
if (hospitalCodeCache.contains(item.getHospitalCode())) {
    // hospitals 테이블엔 있지만 detail은 없는 병원 → 진료과만으로 최소 엔티티 생성
    JpaHospitalDetailEntity newEntity = new JpaHospitalDetailEntity();
    newEntity.setHospitalCode(item.getHospitalCode());
    newEntity.setDepartmentCodes(item.getDepartmentCodes());
    return newEntity;
}
return null; // hospitals 테이블에도 없음 → FK 위반, 저장 불가
```

#### HospitalGradeReader — numOfRows 상향

```java
Long numOfRows = 40000L; // 35962 → 40000 : 실제 수(36,935)보다 여유있게 설정
```

---

### 기존 vs 수정 비교

#### loadHospitalDepartmentStep 흐름 변화

```
[기존]
CSV (비정렬) → 연속 행 그룹핑 → 185,506그룹
                                      │
                              HospitalDetailCache 조회
                                      │
                              없으면 바로 null → 유실


[수정]
CSV (비정렬) → LinkedHashMap 전체 그룹핑 → 정확한 병원 수 그룹
                                                 │
                                         HospitalDetailCache 조회
                                                 │
                                  ┌──────────────┴──────────────┐
                                있음                           없음
                                  │                              │
                              진료과 업데이트            HospitalCodeCache 2차 확인
                                                                 │
                                                   ┌────────────┴────────────┐
                                                 있음                       없음
                                                   │                         │
                                           최소 엔티티 생성               null (FK 불가)
```

### 코드 핵심 변화 요약

| **파일**                        | **기존**              | **수정**                                 |
| :------------------------------ | :-------------------- | :--------------------------------------- |
| GroupedHospitalDepartmentReader | 연속 행 비교 그룹핑   | LinkedHashMap 전체 그룹핑                |
| HospitalDetailJobListener       | afterJob()만 존재     | beforeJob()에서 DB → 캐시 선적재 추가    |
| HospitalDepartmentProcessor     | 캐시 미스 → 즉시 null | 2단계 fallback (DetailCache → CodeCache) |
| HospitalDepartmentWriter        | try-catch 예외 묵인   | 예외 전파 (Batch 프레임워크 위임)        |
| HospitalGradeReader             | numOfRows=35962       | numOfRows=40000                          |

---

### 결과

|     **구분**     | **제공 데이터** |  **기존**   | **리팩토링 후** |   **회수**   |
| :--------------: | :-------------: | :---------: | :-------------: | :----------: |
|    병원 정보     |     78,774      |   78,774    |     78,774      |      -       |
|  병원 상세 정보  |     24,956      |   23,576    |     23,576      |      -       |
| 병원 진료과 정보 |     421,540     |   92,550    |     421,540     | **+328,990** |
|  병원 등급 정보  |     36,935      |   35,836    |     36,803      |   **+967**   |
|     **합계**     |   **562,205**   | **230,736** |   **560,693**   | **+329,957** |

기존 대비 **329,957건 회수**, 전체 유실률 **58.9% → 0.27%** 로 감소.

잔여 유실(1,512건)은 원천 데이터와 hospitals 테이블 간 병원코드 불일치로 인한 것으로, FK 참조 무결성을 지키는 한 애플리케이션 레벨에서 해결할 수 없는 구조적 한계다.

## 주변 병원 데이터 조회

### 문제 상황

- 일부 데이터 제외
  사용자의 위치를 기준으로 반경 3Km 내 병원 정보를 반환해주는 API를 설계하였다.  
  해당 로직은 사용자의 위도, 경도를 받아와 내부에서 정수로 변환한 값으로 DB에서 데이터의 위도, 경도가 정수 부분이 동일한 데이터만을 조회하고 서버에서 Haversine 공식을 사용하여 데이터 필터링을 했다.

```
                사용자의 위도, 경도를 정수로 변환
                            |
        병원의 위도, 경도 정수부분이 동일한 데이터를 조회
                            |
조회한 데이터를 Haversine 공식으로 필터링 (반경 3km 데이터 필터링)
                            |
                        데이터 반환
```

위 같은 로직에서의 문제점은 사용자 기준에서 포홤되어야 할 데이터가 정수 부분의 값이 다르기 때문에 필터링이 되어 전달되지 못 한다는 점이다.

```
a병원 위치 - (36.9999, y)
사용자 위치 - (37.0001, y)
```

해당 케이스의 경우 1차 필터링 조건으로 제외된다.

### 문제 해결

- 1차 문제 해결

위도, 경도의 데이터를 정수로 변환하여 조회하는 방법이 아닌 Bounding Box를 만들어서 데이터 조회하도록 변경하였다.

```
           ┌─────────────────────────┐
           │      BoundingBox        │  ← 1단계: DB가 처리
           │   ┌───────────────┐     │
           │   │  ○   ○    ○   │     │
           │   │ ○          ○  │     │  ← 2단계: App이 처리
           │   │   ○       ○   │     │
           │   └───────────────┘     │
           │  ○               ○      │
           └─────────────────────────┘
```

이렇게 1차적으로 넓은 범위의 데이터를 갖고와서 서버에서 2차적으로 필터링, 정렬을 하여 반환을 하도록 수정을 했다.

해당 리팩토링으로 기존 문제는 해결했다. 하지만, 부하테스트 중 다른 문제가 발생하였다.
`/api/hospital/search` 해당 성능과 비교하였을 때 응답 시간과 크기 차이와 Heap 메모리 사용량이 300MB 가까이 증가한다는 문제가 발견했다.

| 시나리오    | 엔드포인트                               |       p50 (ms)        |         p95 (ms)         |     에러율 (%)     |             응답 크기             |
| :---------- | :--------------------------------------- | :-------------------: | :----------------------: | :----------------: | :-------------------------------: |
| B3-Hospital | GET /api/hospital/search (파라미터 없음) | 16.00 / 16.00 / 16.00 |  27.00 / 27.00 / 25.00   | 0.00 / 0.00 / 0.00 |    5.85 KB / 2.97 KB / 2.97 KB    |
| B3-Hospital | GET /api/hospital/nearby                 | 73.00 / 73.00 / 72.00 | 101.00 / 100.00 / 100.00 | 0.00 / 0.00 / 0.00 | 268.12 KB / 136.15 KB / 136.25 KB |

- 2차 문제 해결

1. 인덱스
   기존 로직에서 DB에 EXPLAIN 쿼리를 사용하여 확인을 하니

```
type: ALL
key: NULL
rows: 71158
Extra: Using where; Using filesort
```

이와 같이 병원 데이터를 풀 스캔을 하여 계산을 하고 있다는 상황을 발견했다. 그래서 인덱스를 설정하여 기존 로직을 수행하고 다시 확인을 했다.

```sql
CREATE INDEX idx_hospital_location
ON hospitals (latitude, longitude);
```

```
type: range
key: idx_hospital_location
rows: 1393
Extra: Using index condition; Using where; Using filesort
```

인덱스 설정 후 풀 스캔을 하지 않고 보다 작은 양의 데이터를 읽도록 향상되었다. (1,393개 읽음)

**_71,158 → 1,393 (약 98% 감소)_**

2. 구면 거리 계산

```sql
-- 변경 전
SELECT * FROM hospitals h
WHERE h.latitude  BETWEEN :minLat AND :maxLat
AND   h.longitude BETWEEN :minLng AND :maxLng
```

기존에는 위 처럼 바운더리 박스를 조회하고 서버에서 구면 거리 계산(Haversine)을 수행한다.  
서버 내에서 추가적인 처리 때문에 응답의 속도가 지체될 것이라고 생각한다. (물론 풀스캔의 영향도 있겠지만) 그렇기 때문에 1차적으로 데이터를 받아올 때 좀 더 타이트한 범위의 데이터를 받아오고 2차적인 필터링을 진행하지 않는 방향으로 개선을 한다면 해당 API의 성능 향상이 될 것이라고 기대한다.

그렇기 때문에 MariaDB에서 `ST_Distance_Sphere`를 제공한다는 것을 찾았다. 그래서 해당 함수를 사용하는 방향으로 수정했다.

```sql
-- 변경 후
SELECT h.*,
    ST_Distance_Sphere(
        POINT(h.longitude, h.latitude),
        POINT(:userLng, :userLat)
    ) AS distance
FROM hospitals h
WHERE h.latitude  BETWEEN :minLat AND :maxLat
AND   h.longitude BETWEEN :minLng AND :maxLng
HAVING distance <= 3000        -- 3km = 3000m (미터 단위)
ORDER BY distance              -- 가장 가까운 순으로 정렬
```

> **ST_Distance_Sphere 설명**
> MariaDB 내장 공간 함수. `POINT(경도, 위도)` 두 점 사이의 구면 거리를 **미터(m)** 단위로 반환.
> BoundingBox로 인덱스 필터를 먼저 타고, 그 안에서만 거리 계산을 수행하므로 Full Scan 없음.

### 결과

| 항목          | 리팩토링 전 | 리팩토링 후 | 개선율      |
| :------------ | :---------- | :---------- | :---------- |
| p50 응답 시간 | 73ms        | 9ms         | **87.7% ↑** |
| p95 응답 시간 | 98.65ms     | 11.33ms     | **88.5% ↑** |
| DB 스캔 rows  | 71,158      | 1,393       | **98.0% ↓** |
| 응답 크기     | 268.12 KB   | 136.15 KB   | **49.2% ↓** |

인덱스로 DB 풀스캔을 제거하고, 구면 거리 계산과 정렬을 DB 레벨로 이전함으로써
**서버의 불필요한 연산을 제거하고 p95 응답 시간을 88.5% 단축했다.**

---

## 로그인 성능 최적화

### 문제 상황

부하 테스트(JMeter, 100 users) 결과 로그인 API의 p95 응답 시간이 **179.32ms** 로 측정되었다.

| 엔드포인트             | p50 (ms) | p95 (ms) |
| :--------------------- | :------- | :------- |
| POST /api/member/login | 130ms    | 179.32ms |

### 문제 분석

로그인 흐름에서 두 가지 병목을 확인했다.

1. BCrypt strength 과다 설정

```java
// 기존 — default strength(10) 사용
@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}
```

BCrypt는 strength가 1 증가할 때마다 연산 횟수가 2배 증가한다. 위에서는 strength설정을 따로 없기 때문에 기본값 10으로 동작하게 된다.  
기준으로 단일 해싱에 약 100ms가 소요된다고 한다, 이 부분이 로그인 응답 시간의 주요 원인 중 하나 이다.

```
strength  연산 횟수    해싱 시간(대략)
8         256회        ~25ms
10        1,024회      ~100ms   ← 기존
12        4,096회      ~400ms
```

2. Refresh Token 이중 조회 및 Insert/Update 분리

```java
// 기존 — 토큰 존재 여부를 별도 조회 후 분기
Optional<RefreshToken> existing = refreshTokenRepository.findByMemberId(memberId);
if (existing.isPresent()) {
    existing.get().updateToken(token);
} else {
    refreshTokenRepository.save(new RefreshToken(memberId, token));
}
```

로그인마다 SELECT → INSERT or UPDATE 진행하는 구조를 갖고 있기 떄문에 하나의 요청에 각각 2번의 쿼리가 발생하고 있다.

### 해결

1. BCrypt strength 8로 조정

```java
// 수정 후
@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder(8);
}
```

strength=8은 OWASP 권고 최솟값으로, 보안성을 유지하면서 해싱 시간을 약 75% 단축한다.

2. Refresh Token Upsert 전환

```java
// 수정 후 — 단일 쿼리로 처리
@Modifying
@Query(value = """
    INSERT INTO refresh_token (member_id, token)
    VALUES (:memberId, :token)
    ON DUPLICATE KEY UPDATE token = :token
    """, nativeQuery = true)
void upsert(@Param("memberId") Long memberId, @Param("token") String token);
```

SELECT + INSERT/UPDATE 2번 → Upsert 1번으로 줄였다.

### 결과

| 항목                       | 리팩토링 전 | 리팩토링 후 | 개선율      |
| :------------------------- | :---------- | :---------- | :---------- |
| POST /api/member/login p95 | 179.32ms    | 81.32ms     | **54.7% ↑** |
| POST /api/member/login p50 | 130ms       | 60ms        | **53.8% ↑** |
| 로그인 당 DB 쿼리 횟수     | 2회         | 1회         | **50% ↓**   |

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
|   loadHospitalDetailStep   |    1,380    | hospitalS에 없는 ykiho → FK 제약으로 저장 불가 (구조적 유실)      |
| loadHospitalDepartmentStep |   365,071   | ① CSV 비정렬로 그룹핑 오류 ② 재실행 시 캐시 비어있음 ③ Writer 예외 묵인 |
|   loadHospitalGradeStep    |    1,099    | ① numOfRows 부족으로 API 미수신               |

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

## 해결 방안

| **문제**                | **해결**                                       |
| :---------------------- | :--------------------------------------------- |
| CSV 비정렬 그룹핑 오류  | 전체 CSV를 LinkedHashMap으로 읽어 완전 그룹핑  |
| 재실행 시 캐시 비어있음 | beforeJob()에서 기존 DB 데이터 캐시 선적재     |
| 캐시 미스 → 무조건 skip | HospitalCodeCache 2차 확인 후 최소 엔티티 생성 |
| Writer 예외 묵인        | try-catch 제거, 예외를 Batch 프레임워크로 전파 |
| numOfRows 부족          | 40,000으로 상향 (실제 수보다 여유 설정)        |

---

## 해결

### GroupedHospitalDepartmentReader — 전체 그룹핑

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

### HospitalDetailJobListener — beforeJob() 추가

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

### HospitalDepartmentProcessor — 2단계 fallback

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

### HospitalGradeReader — numOfRows 상향

```java
Long numOfRows = 40000L; // 35962 → 40000 : 실제 수(36,935)보다 여유있게 설정
```

---

## 기존 vs 수정 비교

### loadHospitalDepartmentStep 흐름 변화

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

## 결과

|     **구분**     | **제공 데이터** |  **기존**   | **리팩토링 후** |   **회수**   |
| :--------------: | :-------------: | :---------: | :-------------: | :----------: |
|    병원 정보     |     78,774      |   78,774    |     78,774      |      -       |
|  병원 상세 정보  |     24,956      |   23,576    |     23,576      |      -       |
| 병원 진료과 정보 |     421,540     |   92,550    |     421,540     | **+328,990** |
|  병원 등급 정보  |     36,935      |   35,836    |     36,803      |   **+967**   |
|     **합계**     |   **562,205**   | **230,736** |   **560,693**   | **+329,957** |

기존 대비 **329,957건 회수**, 전체 유실률 **58.9% → 0.27%** 로 감소.

잔여 유실(1,512건)은 원천 데이터와 hospitals 테이블 간 병원코드 불일치로 인한 것으로, FK 참조 무결성을 지키는 한 애플리케이션 레벨에서 해결할 수 없는 구조적 한계다.

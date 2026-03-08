# 문제점 1. Batch

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

### 문제 해결 시도

- #### Batch Code
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

# 첫 번째 스테이지: 빌드 스테이지
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# build context: back/ 디렉토리 기준 (docker build -f docker/Dockerfile . 으로 실행)
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew build --no-daemon -x test

# 두 번째 스테이지: 실행 스테이지
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/back-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Seoul

# application-secret.yml은 GitHub Actions에서 secrets.APPLICATION_SECRET_YML로 생성된 후
# COPY src ./src 단계에서 이미지에 포함됨 (OpenAI, JWT, Mail 등 민감 설정값 포함)
# 아래 환경변수는 application-prod.yml이 런타임에 참조하는 값으로 컨테이너 실행 시 주입 필요:
#   DATABASE_PASSWORD   - MariaDB 비밀번호 (application-prod.yml: ${database.password})
#   REDIS_PASSWORD      - Redis 비밀번호   (application-prod.yml: ${redis.password})

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "app.jar"]

# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle/
RUN chmod +x gradlew

# Resolve dependencies before copying source (better layer caching)
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true

COPY src src/
RUN ./gradlew bootJar -x test --no-daemon

# Rename to a fixed name, excluding the -plain.jar
RUN find build/libs -name "*.jar" ! -name "*plain*" -exec cp {} app.jar \;

# ── Stage 2: runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/app.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]

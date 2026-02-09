FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    fontconfig-config \
    fonts-dejavu-core \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/life-simulation-1.0-SNAPSHOT.jar /app/life-simulation.jar

COPY simulation-config.json /app/default-config.json

COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh
COPY generate_timelapse.sh /app/generate_timelapse.sh
RUN chmod +x /app/generate_timelapse.sh

RUN mkdir -p /app/config

ENTRYPOINT ["/app/docker-entrypoint.sh"]

# Build stage
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy gradle files
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle ./

# Copy source code BEFORE building
COPY src/ src/

# Download dependencies and build
RUN ./gradlew bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install netstat for health checks
RUN apk add --no-cache net-tools

# Copy built jar from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose ports
EXPOSE 9090

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

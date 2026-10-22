# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy dependency metadata first for better layer caching
COPY pom.xml .

# Download dependencies before copying application source
RUN mvn -B dependency:go-offline

# Copy application source and build the executable JAR
COPY src ./src

RUN mvn -B clean package -DskipTests \
    && cp target/*.jar /app/app.jar


# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create an unprivileged user
RUN groupadd --system spring && useradd --system --gid spring spring

# Copy only the executable JAR from the builder
COPY --from=builder --chown=spring:spring /app/app.jar app.jar

# Document the application port
EXPOSE 8080

# Run as a non-root user
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
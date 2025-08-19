# ================================
# Stage 1: Build the Spring Boot JAR
# ================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy only the pom.xml first to leverage Docker cache for dependencies
COPY email-writer-sb/pom.xml .

# Download dependencies (caches them unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY email-writer-sb ./src

# Build the application JAR
#RUN mvn clean package

# ================================
# Stage 2: Run the Application
# ================================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Run with production profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]



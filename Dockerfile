# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY /email-writer-sb/pom.xml .

# Stage 2: Run
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /email-writer-sb/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]


FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY /email-writer-sb/target/email-writer-sb-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]


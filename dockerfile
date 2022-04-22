FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine as builder
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src


RUN ./mvnw -B package

FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine as packager

COPY --from=builder target/TelegramBotDemo-0.0.1.jar .

EXPOSE 8080
ENTRYPOINT ["java","-jar","TelegramBotDemo-0.0.1.jar"]
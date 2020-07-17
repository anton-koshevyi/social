FROM maven:3.6-jdk-8-alpine AS builder
WORKDIR app
COPY pom-prod.xml pom.xml
RUN mvn dependency:go-offline
COPY src/main src/main/
RUN mvn package --offline

FROM openjdk:8-jre-alpine3.9
RUN addgroup docker
RUN adduser \
    --no-create-home \
    --gecos "" \
    --ingroup docker \
    --disabled-password \
    docker
USER docker:docker
COPY --from=builder app/target/backend-*.jar app/backend.jar
ENTRYPOINT ["java", "-jar", "app/backend.jar"]

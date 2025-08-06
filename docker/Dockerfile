FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

ARG PROFILE
ENV PROFILE=${PROFILE}
ENV APP_NAME=wallet-service.jar
ENV PORT=8080

COPY --from=build /app/target/*.jar ${APP_NAME}

EXPOSE ${PORT}

ENTRYPOINT exec java $JAVA_OPTS -jar $APP_NAME

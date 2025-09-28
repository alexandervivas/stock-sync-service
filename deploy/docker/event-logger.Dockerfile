FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN gradle :modules:event-logger:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/modules/event-logger/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

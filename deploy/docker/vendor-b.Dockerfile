FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN gradle :modules:vendor-b:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/modules/vendor-b/build/libs/*.jar app.jar

VOLUME ["/data"]
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

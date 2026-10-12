FROM eclipse-temurin:21-jre

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY --chown=10001:10001 ${JAR_FILE} app.jar

USER 10001:10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM openjdk:17-jdk

COPY build/libs/*0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/logs
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]

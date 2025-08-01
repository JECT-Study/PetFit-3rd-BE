FROM openjdk:17-jdk

COPY build/libs/*v1.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
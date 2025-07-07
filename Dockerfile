# jdk 17 Image Start
FROM openjdk:17

COPY build/libs/petfit-web-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

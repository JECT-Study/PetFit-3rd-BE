# jdk 17 Image Start
FROM openjdk:17

# 인자 설정 - JAR 파일 이름
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너의 /app.jar로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

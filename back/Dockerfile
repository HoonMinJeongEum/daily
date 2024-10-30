# open jdk 17
FROM openjdk:17

# 타임존 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo Asia/Seoul > /etc/timezone

# build 시점에 JAR_FILE 변수 선언
ARG JAR_FILE=build/libs/daily-0.0.1-SNAPSHOT.jar

# JAR 파일을 daily.jar로 복사
COPY ${JAR_FILE} daily.jar

# resources 디렉토리 및 JSON 파일 복사
COPY src/main/resources/ /app/resources/
COPY src/main/resources/serviceAccountKey.json /app/resources/serviceAccountKey.json

EXPOSE 8081

# 애플리케이션 시작 시 환경 설정 및 타임존 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "-Dspring.config.location=file:/app/resources/", "/daily.jar"]

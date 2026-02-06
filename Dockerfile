# 1. 베이스 이미지 설정 (Java 17 사용)
FROM openjdk:17-jdk

# 2. 빌드된 jar 파일의 위치 변수 설정
# (Gradle로 빌드하면 build/libs 폴더에 jar가 생깁니다)
ARG JAR_FILE=build/libs/*.jar

# 3. jar 파일을 컨테이너 내부로 복사 (이름은 app.jar로 변경)
COPY ${JAR_FILE} app.jar

# 4. 컨테이너 실행 시 동작할 명령어 (java -jar app.jar)
ENTRYPOINT ["java", "-jar", "/app.jar"]
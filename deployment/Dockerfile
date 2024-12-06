FROM eclipse-temurin:21.0.2_13-jdk-jammy
ARG JAR_FILE
ADD ${JAR_FILE}*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
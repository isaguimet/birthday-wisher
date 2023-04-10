FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-Dspring.profiles.active=inst3", "-jar", "/app.jar" ]
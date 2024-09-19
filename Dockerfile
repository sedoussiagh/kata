FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/kata-0.0.1-SNAPSHOT.jar /app/kata.jar

ENV SPRING_PROFILES_ACTIVE=cloud

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app/kata.jar"]
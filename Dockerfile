FROM eclipse-temurin:21-jre

WORKDIR /app

COPY /target/core-0.0.1-SNAPSHOT.jar /app/core.jar

ENTRYPOINT ["java", "-jar", "/app/core.jar", "--spring.profiles.active=dev"]

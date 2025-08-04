# Dockerfile-server
FROM eclipse-temurin:21-jre

COPY ./build/libs/abacusflow-server.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

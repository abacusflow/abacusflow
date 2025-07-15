# Dockerfile-server
FROM eclipse-temurin:17-jre

COPY abacusflow-server/build/libs/abacusflow-server.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

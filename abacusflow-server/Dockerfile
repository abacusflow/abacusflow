# Dockerfile-server
FROM eclipse-temurin:17-jre
ARG VERSION

COPY abacusflow-server/build/libs/abacusflow-server-${VERSION}.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

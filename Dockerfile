# Multi-stage build for Render free tier
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/url-health-status-*.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0 -Xss256k"
EXPOSE 8080
# Render sets PORT; Spring reads server.port from application.properties (${PORT:8080})
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]

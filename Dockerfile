FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy prebuilt JAR file
COPY target/java-data-postgres-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copie le JAR depuis l'hôte (pas depuis un stage build)
COPY target/cashtag-0.0.1.jar .
CMD ["java", "-jar", "-Dspring.profiles.active=production", "cashtag-0.0.1.jar"]
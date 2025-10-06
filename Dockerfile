# Use an temurin with Maven image as the base image
FROM maven:3.9-eclipse-temurin-21-alpine AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .

COPY src ./src
# Build the application using Maven
RUN mvn clean package -DskipTests

# Use an official eclipse temurin image as the base image
FROM eclipse-temurin:21-jre-alpine
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/cashtag-0.0.2.jar .
# Set the command to run the application
CMD ["java", "-jar", "-Dspring.profiles.active=production", "cashtag-0.0.2.jar"]
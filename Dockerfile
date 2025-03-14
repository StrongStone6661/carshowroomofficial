## Stage 1: Build Stage
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

## Stage 2: Run Stage
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/carshowroom-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

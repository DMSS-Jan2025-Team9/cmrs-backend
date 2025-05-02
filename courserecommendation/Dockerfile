# --- Stage 1: Build the application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (to cache them)
COPY pom.xml .

# This layer helps with Docker cache reuse
RUN mvn dependency:go-offline

# Copy rest of the source code
COPY src ./src

# Build the application (this runs mvn clean package)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jdk

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

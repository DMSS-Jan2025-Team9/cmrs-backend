# --- Stage 1: Build the application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (to cache them)
COPY pom.xml .

# Silent dependency download
RUN mvn -B -q dependency:go-offline

# Copy rest of the source code
COPY src ./src

# Run tests & build silently
RUN mvn clean package -DskipTests

# This is where JaCoCo puts the coverage report
# /app/target/site/jacoco



# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Copy the SSL certificate into the container
COPY Dockerbuild/cmrsapp-alb.pem /etc/ssl/certs/alb-certificate.pem

# Verify Java home and cacerts location
RUN echo "Java Home: $JAVA_HOME" && \
    echo "Truststore location: $JAVA_HOME/lib/security/cacerts"

# Import the SSL certificate into Java's cacerts truststore
RUN keytool -import -trustcacerts -alias alb-cert \
    -file /etc/ssl/certs/alb-certificate.pem \
    -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
    
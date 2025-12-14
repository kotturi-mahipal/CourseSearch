# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jar (skipping tests to speed it up)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-noble
WORKDIR /app
# Copy the built jar from the builder stage
# Note: We use a wildcard to grab the jar regardless of version number
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user for security (Best Practice)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Simple entrypoint - just run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
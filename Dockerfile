# Step 1: Build stage (optional if you already have jar)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /srz-shope
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run stage
FROM openjdk:17-jdk-slim
WORKDIR /srz-shope

# Copy JAR from build stage
COPY --from=build /app/target/*.jar srz-shope.jar

# Expose port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "srz-shope.jar"]

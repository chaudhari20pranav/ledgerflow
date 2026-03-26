# Use official Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the application
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/*.jar"]
# Multi-stage build for customer-service.
# Build stage compiles just this module (and the modules it needs) with the wrapper.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
# Copy the whole repo (monorepo build needs sibling modules + parent POM).
COPY . .
RUN chmod +x mvnw && ./mvnw -B -ntp -pl customer-service -am -DskipTests package

# Runtime stage: slim JRE with just the built jar.
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/customer-service/target/customer-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

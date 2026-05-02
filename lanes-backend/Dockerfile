FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseSerialGC \
  -Xss512k \
  -Dspring.jmx.enabled=false \
  -Dserver.tomcat.threads.max=20 \
  -jar app.jar"]
# ----- Build -----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package

# ----- Runtime -----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
CMD ["sh","-c","java -Dserver.port=$PORT -jar /app/app.jar"]

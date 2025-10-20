# ----- Build -----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
# da permisos al wrapper y compila; si faltara el wrapper, instala maven y compila
RUN chmod +x mvnw && ./mvnw -q -DskipTests package || (apt-get update && apt-get install -y maven && mvn -q -DskipTests package)


# ----- Runtime -----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
CMD ["sh","-c","java -Dserver.port=$PORT -jar /app/app.jar"]

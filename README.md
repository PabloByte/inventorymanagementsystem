SpringWeb
=========

A small Spring Boot web application using Spring Data JPA and Thymeleaf.

Configuration
-------------
The application reads configuration from `src/main/resources/application.properties`.

Default (development)
- Uses an in-memory H2 database. Settings in `application.properties`:
  - spring.datasource.url=jdbc:h2:mem:testdb
  - spring.datasource.driverClassName=org.h2.Driver
  - spring.datasource.username=sa
  - spring.datasource.password=
  - spring.jpa.hibernate.ddl-auto=create-drop
  - H2 console enabled at `/h2-console`
  - Server port: 8082

Using MySQL (XAMPP)
- To use your local MySQL instance (XAMPP) instead of H2, update `src/main/resources/application.properties`:
  - Replace datasource URL with: `jdbc:mysql://localhost:3306/SpringWeb_DB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
  - Set `spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver`
  - Set `spring.datasource.username` and `spring.datasource.password` to your MySQL credentials (e.g. `root` and an empty password in XAMPP by default).
  - Change `spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect`
  - For local development, `spring.jpa.hibernate.ddl-auto=update` is convenient; use `validate` or `none` in production.

Quick start (Windows PowerShell)
- Build and run using the included Maven wrapper (no local Maven required):

```powershell
# From repository root (V:\SpringWeb)
./mvnw.cmd clean spring-boot:run
```

- Access the app after startup:
  - Main: http://localhost:8082/
  - H2 console: http://localhost:8082/h2-console (only with default H2 config)
# SpringWeb

A compact Spring Boot web application that demonstrates CRUD web pages + REST endpoints using:
- Spring Boot (web, data-jpa, thymeleaf)
- Spring Data JPA (Hibernate)
- Thymeleaf templates

This README covers configuration, dependencies, quick start (PowerShell), troubleshooting and developer tips.

## Project layout
- `src/main/java` - application source (controllers, entities, repositories)
- `src/main/resources/application.properties` - main configuration
- `src/main/resources/templates` - Thymeleaf templates (UI)
- `pom.xml` - Maven project and dependencies

## Prerequisites
- Java 17+ (the project `pom.xml` sets `<java.version>17</java.version>`)
- Git (optional)
- XAMPP MySQL (optional) if you want to use MySQL instead of H2
- No local Maven required — the project contains the Maven wrapper (`mvnw.cmd`)

## Configuration
All configuration is in `src/main/resources/application.properties`.

Default (development): in-memory H2
- URL: `jdbc:h2:mem:testdb`
- Driver: `org.h2.Driver`
- Username: `sa`
- DDL: `spring.jpa.hibernate.ddl-auto=create-drop`
- H2 console: `/h2-console`
- Server port: `8082`

Using MySQL (XAMPP) instead of H2
1. Create the database in MySQL (XAMPP):

   CREATE DATABASE SpringWeb_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

2. Update `src/main/resources/application.properties` (example):

   spring.datasource.url=jdbc:mysql://localhost:3306/SpringWeb_DB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
   spring.jpa.hibernate.ddl-auto=update

3. Ensure MySQL server is running in XAMPP and the `mysql-connector-j` dependency is present in `pom.xml` (this project already includes it).

Security note: do not store production passwords in `application.properties`. Use environment variables or externalized configuration (see Spring Boot docs).

## Key dependencies (see `pom.xml`)
- spring-boot-starter-web — web and embedded Tomcat
- spring-boot-starter-data-jpa — JPA and Hibernate
- spring-boot-starter-thymeleaf — server-side templates
- spring-boot-starter-validation
- spring-boot-starter-actuator (optional monitoring)
- mysql-connector-j (runtime) — MySQL driver
- lombok (optional, used in the project)

If you need the H2 driver on the classpath add:

```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

## Quick start (Windows PowerShell)
- From the repository root (e.g. `V:\SpringWeb`):

```powershell
# Run the app
./mvnw.cmd clean spring-boot:run

# Build runnable JAR
./mvnw.cmd clean package
java -jar target\SpringWeb-0.0.1-SNAPSHOT.jar
```

- Open in browser when startup finishes:
  - App: http://localhost:8082/
  - H2 console (if using H2): http://localhost:8082/h2-console

## Running tests
- Run unit tests with:

```powershell
./mvnw.cmd test
```

## Troubleshooting
- "Unable to find a single main class": remove duplicate classes containing `public static void main` annotated with `@SpringBootApplication`. Keep the main class `com.springweb.SpringWebApplication`.
- "Cannot load driver class: org.h2.Driver": add H2 dependency or switch to MySQL and ensure the correct driver (`mysql-connector-j`) is on the classpath.
- Database connection failures: verify MySQL service is running, credentials are correct, and the `SpringWeb_DB` database exists.
- Port conflicts: change `server.port` in `application.properties`.

If a build error references multiple main class candidates, search for additional `SpringWebApplication` files and remove or rename duplicates.

## Development tips
- IDE: import as a Maven project (IntelliJ IDEA, Eclipse). Run the `SpringWebApplication` class from your IDE for quick iteration.
- Lombok: enable annotation processing in your IDE if you use Lombok features.
- Use `spring.jpa.hibernate.ddl-auto=update` for local dev; switch to `validate` or `none` for production and manage schema with migrations (Flyway/Liquibase).

## Where to look in the code
- Main class: `src/main/java/com/springweb/SpringWebApplication.java`
- Controllers: `src/main/java/com/springweb/controller`
- Entities: `src/main/java/com/springweb/entity`
- Repositories: `src/main/java/com/springweb/repository`
- Templates: `src/main/resources/templates`

## Want me to configure MySQL now?
If you want, I can update `src/main/resources/application.properties` to point to your XAMPP MySQL (provide username and whether the password is empty), and then run a quick build to verify startup. Do not paste secrets in public workspaces.

---
Edited: concise guide for running and configuring the project locally.

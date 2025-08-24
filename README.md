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

Packaging
- To produce a runnable JAR:

```powershell
./mvnw.cmd clean package
java -jar target\SpringWeb-0.0.1-SNAPSHOT.jar
```

Troubleshooting
- "Unable to find a single main class" — happens when there are multiple classes with a main method. Keep only one `SpringWebApplication` with `@SpringBootApplication`.
- "Cannot load driver class: org.h2.Driver" — add H2 dependency to `pom.xml` or switch to MySQL and ensure the driver is on the classpath (`mysql-connector-j`).
- If using MySQL/XAMPP ensure the database `SpringWeb_DB` exists and the user has permissions.

Notes
- The repository already includes `mysql-connector-j` dependency in `pom.xml`. If you want the H2 driver available, add the `com.h2database:h2` dependency.
- For production, externalize credentials using environment variables or a secrets manager.

Contact
- For help configuring your local MySQL credentials, tell me your username/password and I can suggest exact property values (don't paste secrets here if this workspace is public).

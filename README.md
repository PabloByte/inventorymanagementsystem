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

### Default (development): in-memory H2
- **URL**: `jdbc:h2:mem:testdb`
- **Driver**: `org.h2.Driver`
- **Username**: `sa`
- **DDL**: `spring.jpa.hibernate.ddl-auto=create-drop`
- **H2 console**: `/h2-console`
- **Server port**: `8082`

### Using MySQL (XAMPP) instead of H2
1.  **Create the database** in MySQL (e.g., via phpMyAdmin in XAMPP):
    ```sql
    CREATE DATABASE SpringWeb_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
2.  **Update `src/main/resources/application.properties`** with your MySQL details:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/SpringWeb_DB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
    spring.datasource.username=root
    spring.datasource.password=
    spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
    spring.jpa.hibernate.ddl-auto=update
    ```
3.  **Ensure MySQL server is running** in XAMPP. The `mysql-connector-j` dependency is already included in `pom.xml`.

> **Security Note**: Do not store production passwords in `application.properties`. Use environment variables or a secrets manager for production environments.

## Key Dependencies (from `pom.xml`)
- `spring-boot-starter-web`: For building web applications with an embedded Tomcat server.
- `spring-boot-starter-data-jpa`: For data persistence using JPA and Hibernate.
- `spring-boot-starter-thymeleaf`: For server-side HTML templating.
- `spring-boot-starter-validation`: For data validation.
- `mysql-connector-j`: MySQL JDBC driver (runtime).
- `lombok`: To reduce boilerplate code (optional).

If you need to use the H2 database, ensure the following dependency is in your `pom.xml`:
```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

## Quick Start (Windows PowerShell)
From the project root directory (e.g., `V:\SpringWeb`):

### Run the application
```powershell
./mvnw.cmd clean spring-boot:run
```

### Build a runnable JAR
```powershell
./mvnw.cmd clean package
java -jar target\SpringWeb-0.0.1-SNAPSHOT.jar
```

### Run tests
```powershell
./mvnw.cmd test
```

Once the application is running, you can access it at:
- **Application**: [http://localhost:8082/](http://localhost:8082/)
- **H2 Console** (if using H2): [http://localhost:8082/h2-console](http://localhost:8082/h2-console)

## IDE Quick Start (Java 21)

### JetBrains IntelliJ IDEA
1.  **Prerequisites**: IntelliJ IDEA (Community or Ultimate) with Java 21 JDK configured.
2.  **Open Project**: Go to `File > Open` and select the project's `pom.xml` file or the root folder `SpringWeb`.
3.  **Build**: Allow IntelliJ to resolve Maven dependencies automatically.
4.  **Run**:
    *   Navigate to `src/main/java/com/springweb/SpringWebApplication.java`.
    *   Click the green play icon next to the `main` method to run the application.

### Visual Studio Code
1.  **Prerequisites**: VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) installed and Java 21 JDK configured.
2.  **Open Project**: Go to `File > Open Folder...` and select the `SpringWeb` root folder.
3.  **Build**: The Java extension will automatically detect the Maven project and build it.
4.  **Run**:
    *   Navigate to `src/main/java/com/springweb/SpringWebApplication.java`.
    *   Click the **Run** button that appears above the `main` method.

## Troubleshooting
- **"Unable to find a single main class"**: This error occurs if there are multiple classes with a `main` method annotated with `@SpringBootApplication`. Ensure only `com.springweb.SpringWebApplication` is present.
- **"Cannot load driver class: org.h2.Driver"**: Add the H2 dependency to `pom.xml` or switch to the MySQL configuration.
- **Database Connection Failures**: Verify that your MySQL service is running, the credentials in `application.properties` are correct, and the `SpringWeb_DB` database exists.
- **Port Conflicts**: If port `8082` is in use, change `server.port` in `application.properties`.

## Development Tips
- **IDE Integration**: Import the project as a Maven project in your IDE (e.g., IntelliJ IDEA, VS Code, Eclipse). You can run the `SpringWebApplication` class directly from your IDE for faster development cycles.
- **Lombok**: If you use Lombok, ensure you have the Lombok plugin installed and annotation processing enabled in your IDE.
- **Database Schema**: For local development, `spring.jpa.hibernate.ddl-auto=update` is convenient. For production, it's recommended to use `validate` or `none` and manage schema changes with a migration tool like Flyway or Liquibase.


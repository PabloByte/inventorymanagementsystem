## Spring Web Operation

A compact Spring Boot web application demonstrating CRUD operations with REST endpoints and web pages.

## Quick Start |  Now 123

This guide will help you get "Operation Spring Web" up and running quickly.

### Prerequisites

*   **Java 21**: Ensure you have Java Development Kit (JDK) 21 installed.
*   **Maven**: This project uses Maven for dependency management and building.

### Running the Application

There are two primary ways to run this application:

#### Method 1: Using Maven Wrapper (Command Line)

Navigate to the project's root directory in your terminal and execute the following command:

```bash
./mvnw clean spring-boot:run
```

This command will clean the project, build it, and then start the Spring Boot application.

#### Method 2: Using an IDE (IntelliJ IDEA / VS Code)

1.  **Import the Project**: Open your IDE and import the project as a Maven project.
    *   **IntelliJ IDEA**: Open -> Navigate to the project's `pom.xml` file and select it.
    *   **VS Code**: File -> Open Folder -> Select the project's root directory. Ensure you have the necessary Java extensions installed.
2.  **Run the Application**:
    *   **IntelliJ IDEA**: Locate the `SpringWebApplication.java` file (usually in `src/main/java/com/springweb/`) and run it directly from the IDE (e.g., right-click and select "Run 'SpringWebApplication.main()'").
    *   **VS Code**: Use the "Run" button or the "Run and Debug" view to start the application.

### Accessing the Application

Once the application is running, you can access it in your web browser:

*   **Login Page**: `http://localhost:8082/login` (Default port is 8082)
*   **Dashboard**: `http://localhost:8082/dashboard` (After successful login)

**Demo Credentials**:
*   **Username**: `admin`
*   **Password**: `admin`

## Project Structure

This project utilizes:

*   **Spring Boot**: For rapid application development.
*   **Spring Data JPA**: For database interaction.
*   **Thymeleaf**: For server-side rendered web pages.
*   **H2 Database**: An in-memory database for development purposes.

## Troubleshooting

If you encounter issues, consider the following common solutions:

*   **Port Conflict**: If port 8082 is already in use, you can change it in `src/main/resources/application.properties` by modifying `server.port`.
*   **Java Version**: Ensure your IDE and environment are configured to use Java 21.
*   **Maven Issues**: If dependencies are not resolving, try reloading the Maven project in your IDE or running `mvn clean install` from the command line.

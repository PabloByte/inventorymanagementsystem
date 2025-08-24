# SpringWeb

A compact Spring Boot web application demonstrating CRUD operations with REST endpoints and web pages.

## 🚀 Quick Overview

This project uses:
- **Spring Boot** (web, data-jpa, thymeleaf)
- **Spring Data JPA** with Hibernate
- **Thymeleaf** templates for UI
- **Java 21** (recommended)

---

## 📋 Prerequisites

Before you start, make sure you have:

- ☑️ **Java 21 JDK** (required)
- ☑️ **Git** (for cloning)
- ☑️ **VS Code** or **JetBrains IntelliJ IDEA**
- ☑️ **XAMPP MySQL** (optional - for MySQL database)

---

## 🛠️ IDE Setup Guide

### Option 1: Visual Studio Code

#### 1. Install Required Extensions
Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) which includes:
- Language Support for Java
- Debugger for Java
- Test Runner for Java
- Maven for Java
- Spring Boot Extension Pack

#### 2. Configure Java 21
1. Open VS Code settings (`Ctrl+,` or `Cmd+,`)
2. Search for "java home"
3. Set `java.configuration.runtimes` to point to your Java 21 installation:
   ```json
   "java.configuration.runtimes": [
     {
       "name": "JavaSE-21",
       "path": "/path/to/your/java21"
     }
   ]
   ```

#### 3. Open the Project
1. Open VS Code
2. Go to `File > Open Folder...`
3. Select the `SpringWeb` project folder
4. VS Code will automatically detect the Maven project and download dependencies

#### 4. Run the Application
1. Navigate to `src/main/java/com/springweb/SpringWebApplication.java`
2. Click the **"Run"** button that appears above the `main` method
3. Or use the Command Palette (`Ctrl+Shift+P`) and type "Java: Run"

---

### Option 2: JetBrains IntelliJ IDEA

#### 1. Configure Java 21 SDK
1. Open IntelliJ IDEA
2. Go to `File > Project Structure > Project`
3. Set **Project SDK** to Java 21
4. Set **Project language level** to 21

#### 2. Open the Project
1. Go to `File > Open`
2. Select the project's `pom.xml` file or the root `SpringWeb` folder
3. Choose **"Open as Project"**
4. IntelliJ will automatically import Maven dependencies

#### 3. Verify SDK Configuration
1. Go to `File > Project Structure > Modules`
2. Ensure the **Language level** is set to 21
3. Check that **Dependencies** shows Java 21

#### 4. Run the Application
1. Navigate to `src/main/java/com/springweb/SpringWebApplication.java`
2. Click the green ▶️ icon next to the `main` method
3. Or right-click the file and select **"Run 'SpringWebApplication'"**

---

## 🗂️ Project Structure

```
SpringWeb/
├── src/main/java/           # Java source code
│   └── com/springweb/       # Main package
├── src/main/resources/      # Resources
│   ├── templates/           # Thymeleaf templates
│   └── application.properties  # Configuration
├── pom.xml                  # Maven dependencies
└── mvnw.cmd                # Maven wrapper (Windows)
```

---

## ⚙️ Configuration Options

### Default Setup (H2 In-Memory Database)
No additional setup required! The application uses:
- **Database**: H2 in-memory
- **Port**: 8082
- **H2 Console**: http://localhost:8082/h2-console

### MySQL Setup (Optional)
If you prefer MySQL:

1. **Start XAMPP** and ensure MySQL is running
2. **Create database** in phpMyAdmin:
   ```sql
   CREATE DATABASE SpringWeb_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. **Update** `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/SpringWeb_DB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
   spring.jpa.hibernate.ddl-auto=update
   ```

---

## 🚀 Running the Application

### Method 1: Using Maven Wrapper (Command Line)
```bash
# Windows
./mvnw.cmd clean spring-boot:run

# macOS/Linux
./mvnw clean spring-boot:run
```

### Method 2: Using IDE (Recommended)
- **VS Code**: Click "Run" button above the main method
- **IntelliJ**: Click the green play button next to the main method

### Method 3: Building JAR
```bash
./mvnw.cmd clean package
java -jar target/SpringWeb-0.0.1-SNAPSHOT.jar
```

---

## 🌐 Access the Application

Once running, visit:
- **Main Application**: http://localhost:8082/
- **H2 Database Console**: http://localhost:8082/h2-console (if using H2)

---

## 🧪 Running Tests

```bash
# Command line
./mvnw.cmd test

# VS Code: Use Test Explorer panel
# IntelliJ: Right-click test folder > Run All Tests
```

---

## 🔧 Troubleshooting

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Unable to find a single main class" | Ensure only one class has `@SpringBootApplication` |
| "Cannot load driver class: org.h2.Driver" | Add H2 dependency to `pom.xml` or switch to MySQL |
| Port 8082 already in use | Change `server.port` in `application.properties` |
| Java version conflicts | Verify IDE is using Java 21 SDK |
| Maven dependencies not loading | Reload Maven project in IDE |

### IDE-Specific Issues

#### VS Code
- **Extensions not working**: Restart VS Code after installing Java extensions
- **Syntax errors**: Ensure Java 21 is set in settings
- **Build failures**: Use Command Palette > "Java: Reload Projects"

#### IntelliJ IDEA
- **Red underlines**: File > Invalidate Caches and Restart
- **Maven sync issues**: Click the Maven refresh button in the Maven panel
- **Run configuration missing**: Create new Application run configuration

---

## 📦 Key Dependencies

- `spring-boot-starter-web` - Web application support
- `spring-boot-starter-data-jpa` - Database persistence
- `spring-boot-starter-thymeleaf` - Template engine
- `spring-boot-starter-validation` - Data validation
- `mysql-connector-j` - MySQL driver
- `lombok` - Reduce boilerplate code

---

## 💡 Development Tips

- 🔄 **Hot Reload**: Use Spring Boot DevTools for automatic restarts during development
- 🗃️ **Database**: H2 console is great for development, MySQL for production-like testing  
- 🔧 **Lombok**: Install Lombok plugin in your IDE and enable annotation processing
- 📁 **Project Import**: Always import as Maven project for proper dependency management
- 🐛 **Debugging**: Both IDEs support full debugging with breakpoints
# SpringWeb

A compact Spring Boot web application demonstrating CRUD operations with REST endpoints and web pages.

##  Quick Overview

This project uses:
- **Spring Boot** (web, data-jpa, thymeleaf)
- **Spring Data JPA** with Hibernate
- **Thymeleaf** templates for UI
- **Java 21** (recommended)


##  Running the Application

### Method 1: Using Maven Wrapper (Command Line)
```bash
# Windows
./mvnw.cmd clean spring-boot:run
 

---

##  Troubleshooting

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

this is for testing commit )
##  Development Tips

- 🔄 **Hot Reload**: Use Spring Boot DevTools for automatic restarts during development
- 🗃️ **Database**: H2 console is great for development, MySQL for production-like testing  
- 🔧 **Lombok**: Install Lombok plugin in your IDE and enable annotation processing
- 📁 **Project Import**: Always import as Maven project for proper dependency management
- 🐛 **Debugging**: Both IDEs support full debugging with breakpoints
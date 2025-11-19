# 🚚 LogiTrack - Digital Logistics Supply Chain Management API

A comprehensive Spring Boot REST API for managing digital logistics and supply chain operations, featuring real-time inventory tracking, order management, warehouse operations, and carrier logistics.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Testing & Coverage](#testing--coverage)
- [Configuration](#configuration)
- [Database](#database)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

LogiTrack is a robust API platform designed for managing complex logistics operations. It provides endpoints for:
- **Product Management** - Catalog and SKU management
- **Inventory Control** - Real-time stock tracking and movements
- **Order Processing** - Sales orders, purchase orders, and shipments
- **Warehouse Operations** - Multi-warehouse management
- **Carrier Management** - Shipping logistics
- **User Authentication** - Secure access control

**Version:** 1.0.0  
**Java:** 17+  
**Spring Boot:** 3.2.12  

## ✨ Features

### Core Features
- ✅ RESTful API with full CRUD operations
- ✅ PostgreSQL database with Hibernate ORM
- ✅ JWT-based authentication
- ✅ Input validation and error handling
- ✅ Comprehensive API documentation with Swagger/OpenAPI
- ✅ MapStruct for efficient DTO mapping
- ✅ Lombok for reduced boilerplate code
- ✅ Actuator for health checks and metrics

### Monitoring & Quality
- ✅ JaCoCo test coverage reporting
- ✅ Unit and integration tests
- ✅ Health check endpoints
- ✅ Request logging and debugging

### Developer Experience
- ✅ Interactive Swagger UI
- ✅ OpenAPI 3.0 specification
- ✅ Detailed API documentation
- ✅ Test coverage reports
- ✅ Maven build automation

## 🛠 Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.2.12 |
| **Language** | Java 17 |
| **Build Tool** | Maven 3.9+ |
| **Database** | PostgreSQL 12+ |
| **ORM** | Hibernate 6.4+ |
| **API Docs** | Swagger/OpenAPI 3.0 |
| **Mapping** | MapStruct 1.5.5 |
| **Testing** | JUnit 5, Mockito |
| **Coverage** | JaCoCo 0.8.10 |
| **Server** | Tomcat (embedded) |

## 📦 Prerequisites

Before you begin, ensure you have installed:

- **Java 17+**
  ```bash
  java -version
  ```
  
- **Maven 3.9+**
  ```bash
  mvn -version
  ```
  
- **PostgreSQL 12+**
  ```bash
  psql --version
  ```

- **Git** (for version control)

## 🚀 Installation & Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/roosevelt-conroy/logistiqueTrack.git
cd LogiTrack
```

### Step 2: Configure Database

Create a PostgreSQL database and user:

```sql
CREATE DATABASE logisticsT_db;
CREATE USER root WITH ENCRYPTED PASSWORD 'root';
ALTER ROLE root SET client_encoding TO 'utf8';
ALTER ROLE root SET default_transaction_isolation TO 'read committed';
ALTER ROLE root SET default_transaction_deferrable TO on;
GRANT ALL PRIVILEGES ON DATABASE logisticsT_db TO root;
```

### Step 3: Update Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/logisticsT_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080
spring.application.name=LogiTrack
```

### Step 4: Build the Project

```bash
./mvnw clean install
```

## ▶️ Running the Application

### Option 1: Maven Command
```bash
./mvnw spring-boot:run
```

### Option 2: IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Right-click `LogiTrackApplication.java`
3. Select "Run 'LogiTrackApplication'"

### Option 3: Command Line JAR
```bash
# Build WAR file
./mvnw clean package

# Run the WAR
java -jar target/logitrack-0.0.1-SNAPSHOT.war
```

### Option 4: Docker (if available)
```bash
docker build -t logitrack:latest .
docker run -p 8080:8080 logitrack:latest
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Swagger UI (Interactive)
```
http://localhost:8080/swagger-ui/index.html
```
Test API endpoints directly in your browser with the interactive Swagger UI.

### OpenAPI JSON Specification
```
http://localhost:8080/v3/api-docs
```
Raw OpenAPI 3.0 specification in JSON format.

### Health Check
```
http://localhost:8080/actuator/health
```
Check application and database health status.

### API Status
```
http://localhost:8080/api/status
```
Get current API version and status information.

## 📁 Project Structure

```
LogiTrack/
├── src/
│   ├── main/
│   │   ├── java/com/logitrack/logitrack/
│   │   │   ├── LogiTrackApplication.java      # Main entry point
│   │   │   ├── config/                        # Configuration classes
│   │   │   │   ├── SwaggerConfig.java         # Swagger/OpenAPI config
│   │   │   │   └── WebConfig.java             # Web configuration
│   │   │   ├── controllers/                   # REST endpoints
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── WarehouseController.java
│   │   │   │   └── ...
│   │   │   ├── services/                      # Business logic
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   └── ...
│   │   │   ├── models/                        # JPA entities
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   └── ...
│   │   │   ├── repositories/                  # Data access layer
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── ...
│   │   │   ├── dtos/                          # Data Transfer Objects
│   │   │   │   ├── ProductDTO.java
│   │   │   │   └── ...
│   │   │   ├── mapper/                        # MapStruct mappers
│   │   │   ├── exception/                     # Exception handlers
│   │   │   └── Util/                          # Utility classes
│   │   └── resources/
│   │       ├── application.properties         # Application config
│   │       ├── db/migration/                  # Database migrations
│   │       ├── templates/                     # Thymeleaf templates
│   │       └── static/                        # Static resources
│   └── test/
│       ├── java/com/logitrack/logitrack/
│       │   ├── controllers/                   # Controller tests
│       │   ├── services/                      # Service tests
│       │   └── LogiTrackApplicationTests.java
│       └── resources/
│           └── application-test.properties
├── pom.xml                                    # Maven configuration
├── JACOCO_GUIDE.md                            # Test coverage guide
├── JACOCO_QUICK_START.md                      # Coverage quick start
├── SWAGGER.md                                 # API documentation
├── INTEGRATION_SUMMARY.md                     # Integration guide
└── README.md                                  # This file
```

## 🧪 Testing & Coverage

### Running Tests

**Run all tests:**
```bash
./mvnw test
```

**Run specific test class:**
```bash
./mvnw test -Dtest=ProductServiceTest
```

**Run with coverage report:**
```bash
./mvnw clean test jacoco:report -Dmaven.test.failure.ignore=true
```

### Viewing Coverage Report

After running tests with coverage:

1. Open the coverage report:
   ```
   target/site/jacoco/index.html
   ```

2. View detailed coverage by:
   - Line coverage percentage
   - Branch coverage (if/else paths)
   - Method coverage
   - Per-package and per-class breakdowns

### Coverage Goals
- **Overall Target:** 80%+
- **Critical Code:** 90%+
- **Utilities:** 70%+

For detailed coverage information, see:
- `JACOCO_GUIDE.md` - Comprehensive guide
- `JACOCO_QUICK_START.md` - Quick reference

## ⚙️ Configuration

### Application Properties

**Database:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/logisticsT_db
spring.datasource.username=root
spring.datasource.password=root
```

**JPA/Hibernate:**
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**Server:**
```properties
server.port=8080
server.servlet.context-path=/
```

**Swagger:**
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui/index.html
```

### Environment Variables

```bash
# Optional: Override database via environment
export DB_URL=jdbc:postgresql://localhost:5432/logisticsT_db
export DB_USER=root
export DB_PASSWORD=root
export SERVER_PORT=8080
```

## 🗄️ Database

### Database Schema

The application uses Hibernate to automatically create/update tables:

**Key Tables:**
- `products` - Product catalog
- `inventory` - Stock levels per warehouse
- `warehouses` - Warehouse locations
- `orders` - Sales orders
- `purchase_orders` - Supplier orders
- `users` - System users
- `carriers` - Shipping carriers
- `shipments` - Order shipments

### Database Migrations

Flyway migrations (if used) are located in:
```
src/main/resources/db/migration/
```

### Connecting to Database

```bash
# Using psql CLI
psql -h localhost -U root -d logisticsT_db

# View tables
\dt

# View schema
\d+ products
```

## 🤝 Contributing

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable names
- Add JavaDoc for public methods
- Keep methods focused and short (< 20 lines when possible)

### Before Submitting
```bash
# Format code
./mvnw spotless:apply

# Run tests
./mvnw clean test

# Check coverage
./mvnw clean test jacoco:report

# Build project
./mvnw clean package
```

### Pull Request Process
1. Create a feature branch: `git checkout -b feature/feature-name`
2. Commit changes: `git commit -m "Add feature description"`
3. Push to branch: `git push origin feature/feature-name`
4. Create a Pull Request with description

## 📝 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 📞 Support & Contact

- **Project Issues:** Use GitHub Issues
- **Documentation:** See `SWAGGER.md` for API details
- **Coverage Reports:** Run `./mvnw clean test jacoco:report`
- **Health Status:** Visit `/actuator/health`

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Swagger/OpenAPI 3.0](https://spec.openapis.org/oas/v3.0.0)
- [MapStruct Documentation](https://mapstruct.org/)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/)

## ✅ Quick Start Summary

```bash
# 1. Clone repository
git clone https://github.com/roosevelt-conroy/logistiqueTrack.git
cd LogiTrack

# 2. Setup PostgreSQL database
createdb logisticsT_db

# 3. Build project
./mvnw clean install

# 4. Run application
./mvnw spring-boot:run

# 5. Access API
# Swagger UI: http://localhost:8080/swagger-ui/index.html
# API Docs: http://localhost:8080/v3/api-docs
# Health: http://localhost:8080/actuator/health

# 6. Generate coverage report (optional)
./mvnw clean test jacoco:report
# View: target/site/jacoco/index.html
```

---

**Version:** 1.0.0  
**Last Updated:** November 9, 2025  
**Status:** ✅ Active Development

Happy coding! 🚀

//test jenkins

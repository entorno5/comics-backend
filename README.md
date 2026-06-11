# Comics Backend - RESTful API

Backend application for managing Comics and Users. Built with Spring Boot 3, MongoDB, and modern Java best practices.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Configuration](#configuration)

## ✨ Features

- **User Management**: Create, read, update, delete users with role-based access
- **Comic Management**: Complete CRUD operations for comic books inventory
- **Authentication Ready**: JWT support configured (ready for implementation)
- **Comprehensive Logging**: SLF4J with Logback for application monitoring
- **Input Validation**: Bean Validation with detailed error messages
- **Exception Handling**: Centralized global exception handler with standardized responses
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Pagination Support**: Built-in pagination for list endpoints
- **CORS Support**: Cross-Origin Resource Sharing configuration
- **Database Auditing**: Automatic timestamps for created/updated records
- **Scalable Architecture**: Clean separation of concerns (controllers, services, repositories)

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.5.10
- **Java Version**: Java 17
- **Database**: MongoDB
- **Authentication**: Spring Security + JWT (JJWT)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **ORM/Mapping**: Spring Data MongoDB + MapStruct
- **Logging**: SLF4J with Logback
- **Documentation**: SpringDoc OpenAPI (Swagger 3.0)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ
- **Utilities**: Lombok, Apache Commons Lang

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- MongoDB 4.4 or higher
- Git

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd comics-backend
```

### 2. Configure MongoDB Connection

Edit `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://your-host:27017/testComics
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the interactive API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

### Base API URLs

- **Users**: `http://localhost:8080/api/v1/users`
- **Comics**: `http://localhost:8080/api/v1/comics`

### Example Requests

#### Get All Users (with pagination)

```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=20"
```

#### Create a User

```bash
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "john_doe",
    "name": "John Doe",
    "password": "SecurePass123!",
    "mail": "john@example.com"
  }'
```

#### Get Comic by ID

```bash
curl -X GET "http://localhost:8080/api/v1/comics/{id}"
```

#### Create a Comic

```bash
curl -X POST "http://localhost:8080/api/v1/comics" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spider-Man #1",
    "number": 1,
    "publisher": "Marvel",
    "price": 29.99,
    "description": "The Amazing Spider-Man issue 1",
    "stock": 10
  }'
```

## 📁 Project Structure

```
comics/
├── src/
│   ├── main/
│   │   ├── java/com/comics/backend/
│   │   │   ├── MainApplication.java
│   │   │   ├── config/                 # Configuration beans
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── DevSecurityConfig.java
│   │   │   │   └── NotFoundHandler.java
│   │   │   ├── controllers/            # REST endpoints
│   │   │   │   ├── UserController.java
│   │   │   │   └── ComicController.java
│   │   │   ├── services/               # Business logic
│   │   │   │   ├── UserService.java
│   │   │   │   └── ComicService.java
│   │   │   ├── models/                 # MongoDB entities
│   │   │   │   ├── User.java
│   │   │   │   └── Comic.java
│   │   │   ├── dto/                    # Data Transfer Objects
│   │   │   │   ├── UserResponseDTO.java
│   │   │   │   ├── CreateUserDTO.java
│   │   │   │   ├── ComicResponseDTO.java
│   │   │   │   └── CreateComicDTO.java
│   │   │   ├── repository/             # Data access
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── ComicRepository.java
│   │   │   ├── mappers/                # DTO/Entity mapping
│   │   │   │   └── EntityMapper.java
│   │   │   ├── exceptions/             # Custom exceptions
│   │   │   │   ├── BaseException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── AuthenticationException.java
│   │   │   └── security/               # Security utilities
│   │   └── resources/
│   │       └── application.properties
│   └── test/                           # Unit tests
└── pom.xml                             # Maven configuration
```

## ⚙️ Configuration

### Application Properties

Key configuration properties in `application.properties`:

```properties
# MongoDB Connection
spring.data.mongodb.uri=mongodb://host:port/database

# Logging Levels
logging.level.root=INFO
logging.level.com.comics=DEBUG

# Pagination Defaults
app.pagination.default-size=20
app.pagination.max-size=100

# JWT Configuration (when fully implemented)
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000
```

## 🔒 Security

- **Password Encoding**: BCrypt with 12 rounds
- **CSRF Protection**: Enabled (can be disabled for stateless APIs)
- **CORS**: Configured for cross-origin requests
- **Session Management**: Stateless (required for JWT/stateless APIs)

### Future Security Enhancements

- Implement JWT authentication
- Add OAuth2 integration
- Implement role-based access control (RBAC)
- Add API rate limiting
- Implement audit logging for sensitive operations

## 🧪 Testing

Run unit tests:

```bash
mvn test
```

Run tests with coverage:

```bash
mvn test jacoco:report
```

## 📝 API Endpoints

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | Get all users (paginated) |
| POST | `/api/v1/users` | Create a new user |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users/nickname/{nickname}` | Get user by nickname |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |
| PATCH | `/api/v1/users/{id}/deactivate` | Deactivate user |

### Comics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/comics` | Get all comics (paginated) |
| POST | `/api/v1/comics` | Create a new comic |
| GET | `/api/v1/comics/{id}` | Get comic by ID |
| GET | `/api/v1/comics/title/{title}` | Get comic by title |
| PUT | `/api/v1/comics/{id}` | Update comic |
| DELETE | `/api/v1/comics/{id}` | Delete comic |
| PATCH | `/api/v1/comics/{id}/deactivate` | Deactivate comic |
| PATCH | `/api/v1/comics/{id}/stock` | Update comic stock |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 👥 Authors

- Development Team - Initial work

## 📞 Support

For issues and questions, please create an issue in the repository or contact the development team.

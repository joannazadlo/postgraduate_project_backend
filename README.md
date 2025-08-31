# Backend for RecipeDash

## About the Project

This is the backend service for the RecipeDash App. It provides RESTful APIs for managing users, recipes,
authentication, and integration with external recipe sources.

The backend is built with Java 17 and Spring Boot 3.4.4, and uses PostgreSQL as the primary database.
It supports Firebase Authentication for securing API endpoints.

## Requirements

- Java 17+
- Maven 3.8+
- PostgreSQL

## Technology Stack

- Java 17
- Spring Boot 3.4.4
- Maven for dependency management and build
- PostgreSQL database
- Firebase Admin SDK for authentication
- Spring Security for API protection
- MapStruct for object mapping
- SpringDoc OpenAPI for API documentation
- JUnit and Mockito for testing

## Database setup
Before running the application, make sure you created the database and user defined in `application.properties`

By default, this application expects:
- database: recipedash
- username: postgres
- password: postgres
- host: localhost:5432

## Running the backend locally

1. **Clone the repository**
   Clone the project from GitHub and navigate into the folder

```bash
git clone https://github.com/joannazadlo/postgraduate_project_backend.git
cd postgraduate_project_backend
```

2. **Build the project and download dependencies:**

```bash
mvn clean install
```

3. **Start the backend server**

```bash
mvn spring-boot:run
```

4. By default, the backend runs on http://localhost:8080.

You can access API docs via: http://localhost:8080/swagger-ui/index.html

## Tests
- Run tests with: mvn test
```bash
mvn test
```
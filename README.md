# Library Management System
## Overview
The Library Management System is a comprehensive API built using Spring Boot to facilitate the management of books, patrons, and borrowing records in a library setting. This system provides endpoints for librarians and patrons to perform various operations, including managing books, adding and updating patrons, and handling borrowing transactions.

## Features
### Book Management: 
Add, update, retrieve, and delete books.

### Patron Management: 
Add, update, retrieve, and delete patrons.

### Borrowing Records: 
Allow patrons to borrow and return books.

### Validation and Error Handling:
Input validation and graceful error handling.

### Security: 
Basic authentication or JWT-based authorization to secure endpoints.

### Logging: 
Aspect-Oriented Programming (AOP) for logging method calls and exceptions.

### Transaction Management:
Declarative transaction management to ensure data integrity.

## Getting Started
## Prerequisites
Java 11 or later

Maven or Gradle (for dependency management)

PostgreSQL or another supported database (e.g., MySQL, H2)

## Installation

Clone the Repository

sh

Copy code

git clone https://github.com/AmirMamd/Library-Management-System.git

cd Library-Management-System

Configure the Database

Update src/main/resources/application.properties with your database connection details:

properties

Copy code

spring.datasource.url=jdbc:postgresql://localhost:5432/library_db

spring.datasource.username=your_username

spring.datasource.password=your_password

## Build the Project

sh

Copy code

mvn clean install

Run the Application

sh

Copy code

mvn spring-boot:run

The application will start on http://localhost:8080.

## API Endpoints
### Book Management

GET /api/books: Retrieve a list of all books.

GET /api/books/{id}: Retrieve details of a specific book by ID.

POST /api/books: Add a new book to the library.

PUT /api/books/{id}: Update an existing book's information.

DELETE /api/books/{id}: Remove a book from the library.

### Patron Management

GET /api/patrons: Retrieve a list of all patrons.

GET /api/patrons/{id}: Retrieve details of a specific patron by ID.

POST /api/patrons: Add a new patron to the system.

PUT /api/patrons/{id}: Update an existing patron's information.

DELETE /api/patrons/{id}: Remove a patron from the system.

### Borrowing Records

POST /api/borrow/{bookId}/patron/{patronId}: Allow a patron to borrow a book.

PUT /api/return/{bookId}/patron/{patronId}: Record the return of a borrowed book by a patron.

## Testing APIs

Once the application is running, you can access the Swagger UI at the following URL:

http://localhost:8080/swagger-ui/index.html

Use Postman or any API client to send requests to the API endpoints.

## Testing
Unit tests are written to validate the functionality of API endpoints.

Run unit tests using:

bash

Copy code

mvn test

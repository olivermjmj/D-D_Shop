# D&D Shop Backend

A backend portfolio project built during my 3rd semester in software development.  
The project is designed as a fantasy-themed online shop inspired by Dungeons & Dragons, with focus on backend architecture, persistence, DTO mapping, asynchronous services, and external API integration.

The goal was to build more than a basic CRUD application by working with a larger domain model and a layered architecture using entities, DAOs, services, DTOs, and controllers.

---

## Features

- REST API for managing users, items, orders, and related shop data
- Layered backend architecture
- DAO pattern for persistence
- Service layer with asynchronous methods using `CompletableFuture`
- DTO separation for create, update, and response flows
- JPA / Hibernate persistence
- PostgreSQL database integration
- Import of equipment data from the D&D 5e API
- Price conversion logic for imported equipment
- DAO and service layer testing

---

## Tech Stack

### Backend
- Java 17
- Javalin
- JPA / Hibernate
- Jackson

### Database
- PostgreSQL
- HikariCP

### Tools
- Maven
- Docker
- Git / GitHub

### External API
- D&D 5e API

---

## Architecture

The project follows a layered architecture:

- **Entities** define the domain model
- **DAOs** handle persistence and database queries
- **DTOs** separate input/output models from entities
- **Services** contain business logic, validation, mapping, and async orchestration
- **Controllers** expose the backend through REST endpoints

This structure was chosen to keep responsibilities separated and make the project easier to test, extend, and maintain.

---

## Project Structure

```text
RPG_Shop/
└── src/
    ├── main/
    │   ├── java/app/
    │   │   ├── config
    │   │   ├── controllers
    │   │   ├── dao
    │   │   ├── dto
    │   │   ├── entities
    │   │   ├── exceptions
    │   │   ├── service
    │   │   ├── utils
    │   │   └── Main.java
    │   └── resources/
    └── test/
        └── java/app/

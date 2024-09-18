Carrefour Kata
This project implements a delivery management system for Carrefour, allowing customers to select various delivery methods and book time slots for their orders. The project is built with Spring Boot, uses PostgreSQL for persistent storage, and is containerized with Docker.

Key Features
  Delivery Method Selection: Customers can choose from the following delivery methods:
    DRIVE
    DELIVERY
    DELIVERY_TODAY
    DELIVERY_ASAP

Time Slot Booking: Customers can book a delivery slot based on availability. Each delivery method has different available time slots.
HATEOAS Support: The API is HATEOAS-compliant, providing navigation links within the responses for easier client interaction.
PostgreSQL Database: Persistent storage is handled via PostgreSQL, ensuring data remains available between application restarts.
Dockerized Application: The project is containerized, enabling easy deployment via Docker.

Technology Stack
  Java 21
  Spring Boot 3.x
  Spring Data JPA
  PostgreSQL
  Spring HATEOAS
  Swagger/OpenAPI for API documentation
  JUnit 5 for testing
  Docker for containerization

Getting Started
  Prerequisites
    Ensure the following are installed on your machine:
      
      Java 21 (JDK)
      Maven
      Docker and Docker Compose
      Git

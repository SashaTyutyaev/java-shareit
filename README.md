# Java ShareIt

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![H2 Database](https://img.shields.io/badge/H2-0078D4?style=for-the-badge&logo=h2&logoColor=white)](https://www.h2database.com/)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)

Java ShareIt is an application for renting and exchanging items. The project is implemented in Java and includes a backend using Spring Boot.

## Table of Contents

- [Description](#description)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)

## Description

Java ShareIt is a service that allows users to share their items with others. You can add your items to the catalog, view items from other users, request rentals, and exchange items.

## Technologies

The project uses the following technologies and frameworks:

- Java 11
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- REST API
- Lombok
- JUnit
- Maven
- Postman

## Installation

To install the project on your local machine, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/SashaTyutyaev/java-shareit.git
    ```

2. Navigate to the project directory:
    ```sh
    cd java-shareit
    ```

3. Install dependencies:
    ```sh
    mvn install
    ```

4. Set up PostgreSQL database and modify the connection parameters in the `application.properties` file.

5. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Usage

After successfully running the application, the API will be available at `http://localhost:8080`.

### Sample Requests

- **Get all items:**
    ```sh
    GET /items
    ```

- **Add a new item::**
    ```sh
    POST /items
    {
        "name": "Велосипед",
        "description": "Горный велосипед в хорошем состоянии",
        "available": true
    }
    ```

- **Request to rent an item:**
    ```sh
    POST /items/{itemId}/request
    ```

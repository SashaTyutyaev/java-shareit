# Java ShareIt

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)

Java ShareIt - это приложение для аренды и обмена вещами. Проект реализован на языке Java и включает в себя backend с использованием Spring Boot.

## Оглавление

- [Описание](#описание)
- [Технологии](#технологии)
- [Установка](#установка)
- [Использование](#использование)

## Описание

Java ShareIt - это сервис, который позволяет пользователям делиться своими вещами с другими. Вы можете добавлять свои вещи в каталог, просматривать вещи других пользователей, запрашивать аренду и обмениваться вещами.

## Технологии

Проект использует следующие технологии и фреймворки:

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

## Установка

Для установки проекта на ваш локальный компьютер выполните следующие шаги:

1. Клонируйте репозиторий:
    ```sh
    git clone https://github.com/SashaTyutyaev/java-shareit.git
    ```

2. Перейдите в директорию проекта:
    ```sh
    cd java-shareit
    ```

3. Установите зависимости:
    ```sh
    mvn install
    ```

4. Настройте базу данных PostgreSQL и измените параметры подключения в файле `application.properties`.

5. Запустите приложение:
    ```sh
    mvn spring-boot:run
    ```

## Использование

После успешного запуска приложения, API будет доступен по адресу `http://localhost:8080`.

### Примеры запросов

- **Получить все вещи:**
    ```sh
    GET /items
    ```

- **Добавить новую вещь:**
    ```sh
    POST /items
    {
        "name": "Велосипед",
        "description": "Горный велосипед в хорошем состоянии",
        "available": true
    }
    ```

- **Запросить аренду вещи:**
    ```sh
    POST /items/{itemId}/request
    ```

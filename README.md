# Librery API

## Project Description

LibreryAPI is a backend project developed with Spring Boot that allows managing books, authors, and publishers through
a REST API. In this initial stage, the focus is on implementing business logic without a visual interface, with testing
performed using specialized tools.

## Technologies Used

- **Java**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **MySQL**
- **Lombok** (suggested to reduce boilerplate code)

## Initial Setup

### Steps to Start the Project

1. **Clone the repository** and open the project in your favorite IDE.
2. **Ensure MySQL is installed** and create a database named `libreriaapibbdd`.
3. **Configure the `application.properties` file** in `src/main/resources` with the following values:

```properties
spring.application.name=libreriaapi
spring.datasource.url=jdbc:mysql://localhost:3306/libreriaapibbdd?allowPublicKeyRetrieval=true&useSSL=false&useTimezone=true&serverTimezone=GMT&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.thymeleaf.cache=false
```

## ⚙️ Run the Project

Run the project and verify that the tables are correctly generated in the database.

## 📂 Project Structure

The project follows an organized package structure:

```bash
📦 libreriaapi
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java
 ┃ ┃ ┃ ┣ 📂 com.example.libreriaapi
 ┃ ┃ ┃ ┃ ┣ 📂 entity       # Entities: Book, Author, Publisher
 ┃ ┃ ┃ ┃ ┣ 📂 repository   # JPA Repositories
 ┃ ┃ ┃ ┃ ┣ 📂 service      # Business logic for Author
 ┃ ┃ ┃ ┃ ┣ 📂 controller   # (To be implemented in the next stage)
 ┃ ┃ ┣ 📂 resources
 ┃ ┃ ┃ ┣ 📜 application.properties
```

## 🗂 Database Schema

The project includes three main entities:

### 📘 Book (`Libro`)

| Field             | Type        | Description                |
|-------------------|-------------|----------------------------|
| `isbn`            | String (PK) | Book's unique identifier   |
| `title`           | String      | Book title                 |
| `year`            | Integer     | Year of publication        |
| `copies`          | Integer     | Total copies available     |
| `borrowedCopies`  | Integer     | Number of borrowed copies  |
| `remainingCopies` | Integer     | Remaining available copies |
| `high`            | Boolean     | Active status              |
| `author_id`       | Long (FK)   | Associated author          |
| `publisher_id`    | Long (FK)   | Associated publisher       |

### 🧑‍🎓 Author (`Autor`)

| Field  | Type      | Description                 |
|--------|-----------|-----------------------------|
| `id`   | Long (PK) | Auto-generated ID           |
| `name` | String    | Author's name               |
| `high` | Boolean   | Active status (soft delete) |

### 🏢 Publisher (`Editorial`)

| Field  | Type      | Description                 |
|--------|-----------|-----------------------------|
| `id`   | Long (PK) | Auto-generated ID           |
| `name` | String    | Publisher's name            |
| `high` | Boolean   | Active status (soft delete) |

## 📑 Repository Implementation

The repositories provide **CRUD (Create, Read, Update, Delete)** operations for each entity. No custom queries are
required at this stage.

## 🏗 Service Implementation

### ✅ Implemented Functionalities for `Author`

- **Create** an author.
- **Retrieve** all authors or a specific one.
- **Update** author information.
- **Deactivate** an author (by setting `high = false` instead of deleting it).

## 🚀 Running the Application

### 🧪 How to Test the API

1. Start the application with:
   ```sh
   mvn spring-boot:run
    ```

## 🧪 How to Test the API
1. Use **Postman**, **cURL**, or any **API testing tool** to send requests.
2. Ensure the database **persists data correctly in MySQL**.

## 📌 Future Implementations
### 🔜 Upcoming Features
- ✅ Adding **validations** to services.
- ✅ Implementing **REST controllers** to expose the API.
- ✅ Expanding services for **Book** and **Publisher**.
- ✅ Implementing **custom queries** for advanced searches.
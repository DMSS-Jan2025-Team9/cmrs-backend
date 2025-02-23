# Backend

This is a **Maven Project** using **Java 21**,  **Maven 3.9.9** and **Spring Boot 3.4.2**.

## Running the Microservices
To start a microservice, navigate to its folder and run:

```sh
mvn spring-boot:run
```

### Microservice Ports
Each microservice runs on a different port:

| Microservice             | URL               |
|--------------------------|-------------------|
| **Course Management**    | `localhost:8081`  |
| **Course Recommendation** | `localhost:8082` |
| **Course Registration**  | `localhost:8083`  |
| **Notification Service** | `localhost:8084`  |
| **User Management**      | `localhost:8085`  |

---

## Database Configuration
- Each microservice’s **database script** is located in the `DBScript` folder.
- Run the **SQL script** in **MySQL Workbench** to create the database and insert sample data.

### Updating Database Connection
Modify `application.properties` to match your MySQL credentials:

```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

## Swagger UI
To access:
`http://localhost:<port>/swagger-ui/index.html`

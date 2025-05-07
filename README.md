# Backend

This is a **Maven Project** using **Java 21**, **Maven 3.9.9**, **Spring Boot 3.4.2** and **Docker**.

## Setting up Docker

Ensure Docker Desktop is installed https://www.docker.com/products/docker-desktop/.

Bring up the run window and enter the following:

```
%UserProfile%\.wslconfig
```

Copy and paste the following into notepad:

```
[wsl2]
memory=4GB
swap=2GB
processors=2
```

Save the file and restart Docker Desktop.

## Running the Containers

```
docker-compose build
```

```
docker-compose up
```

## Running the Microservices

To start a microservice, navigate to its folder and run:

```sh
mvn spring-boot:run
```

### Microservice Ports

Each microservice runs on a different port:

| Microservice              | URL              |
| ------------------------- | ---------------- |
| **Course Management**     | `localhost:8081` |
| **Course Recommendation** | `localhost:8082` |
| **Course Registration**   | `localhost:8083` |
| **Notification Service**  | `localhost:8084` |
| **User Management**       | `localhost:8085` |

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


# GitHub Actions
use self-hosted runner = runs-on: self-hosted

connect to the AWS EC2 Instance =  i-071f67013d9eed6b1 (cmrs-ops-console) 
Navigate to the ec2 instance = /home/ubuntu/actions-runner
Ensure the service is up and running - sudo ./svc.sh status
If the service is not running, run sudo ./svc.sh start


Setup self-hosted runner and installed necessary packages:
1) sudo apt-get update
2) # ADD jq  
sudo apt-get install -y jq  # Install jq
3) # ADD DOCKER 
# Install dependencies
sudo apt-get install \
  apt-transport-https \
  ca-certificates \
  curl \
  software-properties-common

  
# Add Docker’s official GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# Set up the stable Docker repository
sudo add-apt-repository \
  "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) \
  stable"

# Update the apt package index again
sudo apt-get update

# Install the latest version of Docker CE (Community Edition)
sudo apt-get install docker-ce

# Verify docker version
docker --version

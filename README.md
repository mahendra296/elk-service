# ELK Service

A microservices-based application built with Spring Boot that manages users and departments. The project follows a distributed architecture with multiple independent services that communicate with each other via REST APIs.

## Project Structure

```
elk-service/
├── user-service/          # User management microservice (Port: 8080)
├── department-service/    # Department management microservice (Port: 8081)
├── common-service/        # Shared utilities, models, DTOs, and exception handling
└── pom.xml               # Parent POM file
```

## Technologies

- **Spring Boot 2.7.8**
- **Java 16**
- **Maven**
- **Spring Data JPA**
- **MySQL**
- **Lombok**
- **Docker**

## Features

### User Service
- Create, update, and retrieve users
- Fetches associated department information via inter-service REST calls
- Distributed tracing with eventTraceId

### Department Service
- Create, update, and retrieve departments
- Standalone department management

### Common Service
- Shared data models (User, Department)
- Data Transfer Objects (UserDTO, DepartmentDTO)
- Global exception handling
- Request/Response interceptors for tracing

## API Endpoints

### User Service (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/hello` | Health check |
| POST | `/api/v1/user` | Create a new user |
| PUT | `/api/v1/user/{userId}` | Update user by ID |
| GET | `/api/v1/user` | Get all users |
| GET | `/api/v1/user/{userId}` | Get user by ID with department info |

### Department Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/hello` | Health check |
| POST | `/api/v1/department` | Create a new department |
| PUT | `/api/v1/department/{departmentId}` | Update department by ID |
| GET | `/api/v1/department` | Get all departments |
| GET | `/api/v1/department/{departmentId}` | Get department by ID |

## Prerequisites

- Java 16 or higher
- Maven 3.6 or higher
- MySQL 5.7 or higher
- Docker (optional)

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_HOST` | MySQL server hostname | localhost |
| `MYSQL_PORT` | MySQL server port | 3306 |
| `MYSQL_DATABASE` | Database name | userdb / departmentdb |
| `MYSQL_USER` | Database username | root |
| `MYSQL_PASSWORD` | Database password | root |
| `DEPARTMENT_URL` | Department service URL (user-service only) | http://localhost:8081 |

## Build & Run

### Build
```bash
mvn clean package
```

### Run Locally
```bash
# Start Department Service first
cd department-service
mvn spring-boot:run

# Start User Service
cd user-service
mvn spring-boot:run
```

### Docker
Both services include Dockerfiles for containerized deployment:

```bash
# Build and run user-service
cd user-service
docker build -t user-service .
docker run -p 8080:8080 user-service

# Build and run department-service
cd department-service
docker build -t department-service .
docker run -p 8081:8081 department-service
```

## Data Models

### User
```json
{
  "id": 1,
  "firstName": "string",
  "lastName": "string",
  "gender": "string",
  "age": 0,
  "departmentId": 1,
  "department": { }
}
```

### Department
```json
{
  "id": 1,
  "departmentName": "string"
}
```

## Architecture

- **Service Layer Pattern**: Service interfaces with implementations
- **Repository Pattern**: Data access layer using Spring Data JPA
- **DTO Pattern**: Separation between internal models and API responses
- **Global Exception Handler**: Centralized exception management with custom exceptions (ResourceNotFoundException, InternalServerException, InvalidRequestException, AuthorizationException)
- **Interceptor Pattern**: Cross-cutting concerns for logging and distributed tracing via eventTraceId

## Logging Configuration

### Logback Configuration

Both services use Logback for logging with the following features:

**Log Pattern:**
```
[yyyy-MM-dd HH:mm:ss.SSS] [LEVEL] [eventTraceId]: ClassName: message
```

**Configuration Files:**
- `user-service/src/main/resources/logback.xml`
- `department-service/src/main/resources/logback.xml`

**User Service Logback Configuration:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="FILE_LOG_PATTERN" value="[%date{yyyy-MM-dd HH:mm:ss.SSS}] [%5.5level] [%X{eventTraceId}]: %c{1}: %msg%n"/>
    <!-- For windows -->
    <!--<property name="LOG_FILE_PATH" value="c:/logs"/>-->
    <property name="LOG_FILE_PATH" value="/var/logs"/>
    <property name="LOG_FILE_NAME" value="user_service.log"/>

    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE_PATH}/${LOG_FILE_NAME}</file>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE_PATH}/${LOG_FILE_NAME}-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>1MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="Console" />
        <appender-ref ref="RollingFile" />
    </root>
</configuration>
```

**Key Features:**
| Feature | Description |
|---------|-------------|
| Console Appender | Outputs logs to stdout |
| Rolling File Appender | Writes logs to file with rotation |
| eventTraceId | MDC-based distributed tracing ID included in every log entry |
| Max File Size | 1MB per log file |
| Max History | 30 days of log retention |
| Log Level | INFO (configurable) |

**Log File Locations:**
| Service | Log File Path |
|---------|---------------|
| User Service | `/var/logs/user_service.log` |
| Department Service | `/var/logs/department_service.log` |

> **Note:** For Windows, uncomment `<property name="LOG_FILE_PATH" value="c:/logs"/>` in the logback.xml file.

## ELK Stack Integration

The ELK (Elasticsearch, Logstash, Kibana) stack can be used to centralize and visualize logs from both microservices.

### Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐
│  User Service   │     │ Department Svc  │
│   (Port 8080)   │     │   (Port 8081)   │
└────────┬────────┘     └────────┬────────┘
         │                       │
         │    Log Files          │
         ▼                       ▼
    /var/logs/              /var/logs/
    user_service.log        department_service.log
         │                       │
         └───────────┬───────────┘
                     │
                     ▼
            ┌─────────────────┐
            │    Logstash     │
            │   (Port 5044)   │
            └────────┬────────┘
                     │
                     ▼
            ┌─────────────────┐
            │  Elasticsearch  │
            │   (Port 9200)   │
            └────────┬────────┘
                     │
                     ▼
            ┌─────────────────┐
            │     Kibana      │
            │   (Port 5601)   │
            └─────────────────┘
```

---

## Option 1: Local Windows Setup

### Prerequisites

Download the following from Elastic's official website:
- **Elasticsearch**: https://www.elastic.co/downloads/elasticsearch
- **Kibana**: https://www.elastic.co/downloads/kibana
- **Logstash**: https://www.elastic.co/downloads/logstash

Extract all zip files to `D:/elastic/` folder and rename the extracted folders to `elasticsearch`, `kibana`, and `logstash`.

### Step 1: Configure and Run Elasticsearch

1. Navigate to `D:/elastic/elasticsearch/bin` and run:
   ```cmd
   elasticsearch.bat
   ```

2. Save the credential details (elastic user password and kibana auth token) from the logs.

3. Disable security for local development. Edit `D:\elastic\elasticsearch\config\elasticsearch.yml`:
   ```yaml
   action.auto_create_index: .monitoring*,.watches,.triggered_watches,.watcher-history*,.ml*

   xpack.security.http.ssl:
     enabled: false

   xpack.security.transport.ssl:
     enabled: false
   ```

4. Restart Elasticsearch. It will now run on http://localhost:9200/

### Step 2: Configure and Run Kibana

1. Edit `D:\elastic\kibana\config\kibana.yml` and uncomment/update:
   ```yaml
   elasticsearch.hosts: ["http://localhost:9200"]
   elasticsearch.username: "kibana_system"
   elasticsearch.password: "your_password"
   ```

2. Navigate to `D:\elastic\kibana\bin` and run:
   ```cmd
   kibana.bat
   ```

3. Access Kibana UI at http://localhost:5601/ and login with elastic user credentials.

4. Go to **Dev Tools** and enable auto_create_index:
   ```
   PUT _cluster/settings
   {
     "persistent": {
       "action.auto_create_index": "true"
     }
   }
   ```

### Step 3: Configure and Run Logstash

1. Create `logstash.conf` file in `D:\elastic\logstash\config`:
   ```conf
   input {
     file {
       type => "department-service"
       path => "c:/logs/department_service.log"
       start_position => "beginning"
     }
     file {
       type => "user-service"
       path => "c:/logs/user_service.log"
       start_position => "beginning"
     }
   }

   output {
     elasticsearch {
       hosts => ["http://localhost:9200"]
       index => "elk-service-logs"
       user => "elastic"
       password => "your_password"
     }
     stdout { codec => rubydebug }
   }
   ```

2. Navigate to `D:\elastic\logstash\bin` and run:
   ```cmd
   logstash.bat -f ../config/logstash.conf
   ```

### Step 4: Create Data View in Kibana

1. Open Kibana at http://localhost:5601
2. Go to **Discover** menu
3. Create a new **Data View** with pattern: `elk-service-logs*`
4. View and filter logs as needed

### Useful Elasticsearch Commands

```cmd
# Generate new enrollment token
D:\elastic\elasticsearch\bin\elasticsearch-create-enrollment-token -s node

# Reset password for users (elastic, kibana, kibana_system)
D:\elastic\elasticsearch\bin\elasticsearch-reset-password -u {user}
```

---

## Option 2: Docker Setup (Windows)

### Prerequisites

1. Install Docker Desktop for Windows: https://docs.docker.com/desktop/install/windows-install/

2. Set max_map_count in WSL (required for Elasticsearch):
   ```powershell
   wsl -d docker-desktop
   sysctl -w vm.max_map_count=262144
   ```

### Step 1: Create Docker Network and Volume

```bash
# Create network for services
docker network create spring-net

# Create network for ELK stack
docker network create elastic

# Create volume for sharing logs between containers
docker volume create log
```

### Step 2: Run MySQL

```bash
# Pull MySQL image
docker pull mysql:8.0.26

# Run MySQL container
docker run --name mysql -d -p 3307:3306 \
  -e MYSQL_ROOT_USER=root \
  -e MYSQL_ROOT_PASSWORD=root \
  mysql:8.0.26

# Connect MySQL to spring-net network
docker network connect spring-net mysql
```

### Step 3: Build and Run Microservices

```bash
# Build user-service image
cd D:\Projects\Test\Code\elk-service\user-service
docker build -t user_service .

# Build department-service image
cd D:\Projects\Test\Code\elk-service\department-service
docker build -t department_service .

# Run user-service container
docker run --name user_container \
  -e MYSQL_HOST=mysql \
  -v log:/var/logs \
  --net spring-net \
  -d -p 8080:8080 \
  user_service

# Run department-service container
docker run --name department_container \
  -e MYSQL_HOST=mysql \
  -v log:/var/logs \
  --net spring-net \
  -d -p 8081:8081 \
  department_service
```

### Step 4: Pull and Run ELK Stack Images

```bash
# Pull ELK images
docker pull docker.elastic.co/elasticsearch/elasticsearch:8.9.1
docker pull docker.elastic.co/kibana/kibana:8.9.1
docker pull docker.elastic.co/logstash/logstash:8.9.1

# Run Elasticsearch
docker run --name elasticsearch --net elastic \
  -d -p 9200:9200 -p 9300:9300 \
  -e discovery.type=single-node \
  -e xpack.security.enabled=false \
  -e xpack.security.enrollment.enabled=false \
  -it docker.elastic.co/elasticsearch/elasticsearch:8.9.1

# Run Kibana
docker run --name kibana --net elastic \
  -d -p 5601:5601 \
  docker.elastic.co/kibana/kibana:8.9.1
```

### Step 5: Configure and Run Logstash

1. Create `logstash.conf` in `D:/docker/`:
   ```conf
   input {
     file {
       type => "department-service"
       path => "/var/logs/department_service.log"
       start_position => "beginning"
     }
     file {
       type => "user-service"
       path => "/var/logs/user_service.log"
       start_position => "beginning"
     }
   }

   output {
     elasticsearch {
       hosts => ["http://elasticsearch:9200"]
       index => "elk-service-logs"
     }
     stdout { codec => rubydebug }
   }
   ```

2. Run Logstash:
   ```bash
   docker run --name logstash \
     --link elasticsearch \
     --net elastic \
     -v log:/var/logs/ \
     -d -it \
     -v D:/docker/:/usr/share/logstash/pipeline/ \
     docker.elastic.co/logstash/logstash:8.9.1
   ```

### Step 6: Setup Kibana

1. Open Kibana at http://localhost:5601
2. Go to **Dev Tools** and enable auto_create_index
3. Go to **Discover** → Create **Data View** with pattern `elk-service-logs*`
4. View and filter logs

### Useful Docker Commands for ELK Security

```bash
# Reset kibana_system password
docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-reset-password -u kibana_system

# Reset elastic user password
docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-reset-password -u elastic

# Generate Kibana enrollment token
docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token -s kibana
```

---

## Docker Commands Reference

| Command | Description |
|---------|-------------|
| `docker build -t {imagename} .` | Build image from Dockerfile |
| `docker build -f {dockerfile path}` | Build with specific Dockerfile |
| `docker images` | List all images |
| `docker rmi {imageId}` | Remove image |
| `docker run -d {image} -p {port}:{Cport}` | Run container in background |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers |
| `docker start {container}` | Start existing container |
| `docker stop {container}` | Stop running container |
| `docker kill {container}` | Kill running container |
| `docker rm {containerId}` | Remove container |
| `docker rm -f {containerId}` | Force remove container |
| `docker exec -it {containerId} bash` | Enter container shell |
| `docker network ls` | List networks |
| `docker network create {name}` | Create network |
| `docker network rm {name}` | Remove network |
| `docker volume ls` | List volumes |
| `docker volume create {name}` | Create volume |
| `docker volume rm {name}` | Remove volume |
| `docker container prune` | Remove all stopped containers |
| `docker rename {old} {new}` | Rename container |

---

## Accessing Services

| Service | URL |
|---------|-----|
| User Service API | http://localhost:8080/api/v1/user |
| Department Service API | http://localhost:8081/api/v1/department |
| Elasticsearch | http://localhost:9200 |
| Kibana Dashboard | http://localhost:5601 |
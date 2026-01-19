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

### Elasticsearch Configuration

Create `elasticsearch/elasticsearch.yml`:
```yaml
cluster.name: elk-service-cluster
node.name: elk-node-1
network.host: 0.0.0.0
http.port: 9200
discovery.type: single-node
xpack.security.enabled: false

# Index settings
indices.query.bool.max_clause_count: 4096

# Path settings
path.data: /usr/share/elasticsearch/data
path.logs: /usr/share/elasticsearch/logs
```

### Logstash Configuration

Create `logstash/logstash.conf`:
```conf
input {
  file {
    path => "/var/logs/user_service.log"
    start_position => "beginning"
    sincedb_path => "/dev/null"
    type => "user-service"
    codec => plain { charset => "UTF-8" }
  }
  file {
    path => "/var/logs/department_service.log"
    start_position => "beginning"
    sincedb_path => "/dev/null"
    type => "department-service"
    codec => plain { charset => "UTF-8" }
  }
}

filter {
  grok {
    match => {
      "message" => "\[%{TIMESTAMP_ISO8601:timestamp}\] \[%{LOGLEVEL:level}\] \[%{DATA:eventTraceId}\]: %{DATA:class}: %{GREEDYDATA:log_message}"
    }
  }
  date {
    match => ["timestamp", "yyyy-MM-dd HH:mm:ss.SSS"]
    target => "@timestamp"
  }
  mutate {
    remove_field => ["timestamp"]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "elk-service-logs-%{+YYYY.MM.dd}"
  }
  stdout { codec => rubydebug }
}
```

Create `logstash/pipelines.yml`:
```yaml
- pipeline.id: elk-service
  path.config: "/usr/share/logstash/pipeline/logstash.conf"
```

### Kibana Configuration

Create `kibana/kibana.yml`:
```yaml
server.name: kibana
server.host: "0.0.0.0"
server.port: 5601
elasticsearch.hosts: ["http://elasticsearch:9200"]

# Logging
logging.dest: stdout
logging.verbose: false

# Index pattern
xpack.monitoring.ui.container.elasticsearch.enabled: true
```

### Docker Compose for ELK Stack

Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - elk-network

  # Elasticsearch
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
      - ./elasticsearch/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
    networks:
      - elk-network

  # Logstash
  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    container_name: logstash
    ports:
      - "5044:5044"
      - "9600:9600"
    volumes:
      - ./logstash/logstash.conf:/usr/share/logstash/pipeline/logstash.conf
      - ./logstash/pipelines.yml:/usr/share/logstash/config/pipelines.yml
      - logs_volume:/var/logs
    depends_on:
      - elasticsearch
    networks:
      - elk-network

  # Kibana
  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    container_name: kibana
    ports:
      - "5601:5601"
    volumes:
      - ./kibana/kibana.yml:/usr/share/kibana/config/kibana.yml
    depends_on:
      - elasticsearch
    networks:
      - elk-network

  # Department Service
  department-service:
    build: ./department-service
    container_name: department-service
    ports:
      - "8081:8081"
    environment:
      - MYSQL_HOST=mysql
      - MYSQL_PORT=3306
      - MYSQL_DATABASE=departmentdb
      - MYSQL_USER=root
      - MYSQL_PASSWORD=root
    volumes:
      - logs_volume:/var/logs
    depends_on:
      - mysql
    networks:
      - elk-network

  # User Service
  user-service:
    build: ./user-service
    container_name: user-service
    ports:
      - "8080:8080"
    environment:
      - MYSQL_HOST=mysql
      - MYSQL_PORT=3306
      - MYSQL_DATABASE=userdb
      - MYSQL_USER=root
      - MYSQL_PASSWORD=root
      - DEPARTMENT_URL=http://department-service:8081
    volumes:
      - logs_volume:/var/logs
    depends_on:
      - mysql
      - department-service
    networks:
      - elk-network

networks:
  elk-network:
    driver: bridge

volumes:
  mysql_data:
  elasticsearch_data:
  logs_volume:
```

### Running the Full Stack

```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Accessing Services

| Service | URL |
|---------|-----|
| User Service API | http://localhost:8080/api/v1/user |
| Department Service API | http://localhost:8081/api/v1/department |
| Elasticsearch | http://localhost:9200 |
| Kibana Dashboard | http://localhost:5601 |
| Logstash Metrics | http://localhost:9600 |

### Kibana Setup

1. Open Kibana at http://localhost:5601
2. Go to **Management** → **Stack Management** → **Kibana** → **Data Views**
3. Create a new data view with pattern: `elk-service-logs-*`
4. Select `@timestamp` as the time field
5. Go to **Analytics** → **Discover** to view logs
6. Create dashboards to visualize:
   - Log volume over time
   - Error rate by service
   - Request tracing by eventTraceId
   - Log level distribution
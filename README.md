# User Activity Tracking System with Spring Boot and Apache Kafka

A production-ready system for tracking user activity events using Spring Boot 3, Apache Kafka, and Docker. It supports real-time event ingestion, validation, storage, retrieval, and stats aggregation, with built-in Dead-Letter Topic (DLT) handling for poison-pill messages.

## Features

- **Event ingestion** via REST API with validation:
  - Allowed actions: `PAGE_VIEW`, `CLICK`, `PURCHASE`, `LOGOUT`
  - Required fields: `userId`, `action`, `resourceId`, `timestamp`
- **Real-time Kafka streaming**:
  - Producer sends `ActivityEvent` to Kafka topic `user-activity-events`
  - Consumer processes events and stores them in-memory
- **DLT (Dead-Letter Topic) handling**:
  - Invalid events (e.g., null `userId`) are routed to `user-activity-events.DLT`
  - Poison-pill messages are not retried indefinitely
- **Activity queries**:
  - `GET /api/activity/{userId}` -> list all events for a user
  - `GET /api/activity/stats` -> aggregated stats (total events, per-action counts)
- **Health & monitoring**:
  - `/actuator/health` for Docker healthcheck
  - `/actuator/info` for app metadata

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.0
- **Messaging**: Apache Kafka (with Spring Kafka)
- **Validation**: Spring Boot Validation (`jakarta.validation`)
- **Monitoring**: Spring Boot Actuator
- **JSON**: Jackson (`jackson-core`, `jackson-databind`, `jackson-annotations`)
- **Deployment**: Docker + Docker Compose

## Project Structure

```text
src/main/java/com/example/useractivity/
  config/
    KafkaConsumerConfig.java      # Kafka consumer, error handler, DLT routing
    KafkaTopicConfig.java         # Kafka topics (user-activity-events + DLT)
  controller/
    ActivityController.java       # REST endpoints
  dto/
    ActivityEvent.java            # Event DTO with validation
    ActivityStats.java            # Stats response
    ProducerResponse.java         # Producer acknowledgment
  service/
    ActivityConsumer.java         # Kafka listener (routes DLT for null userId)
    ActivityProducer.java         # Kafka producer (sends events)
    ActivityStore.java            # In-memory storage + stats
src/main/resources/
  application.yml                 # Local config (Kafka, Actuator)
  application-docker.yml          # Docker config (Kafka brokers)
Dockerfile
pom.xml
docker-compose.yml
README.md
```

## Prerequisites

- JDK 17+
- Maven 3.8+
- Docker & Docker Compose

## How to Run

### 1. Build the project

```powershell
mvn clean
mvn -q compile
```

Or to build a full JAR:

```powershell
mvn clean package
```

### 2. Start Kafka + App with Docker Compose

From the project root:

```powershell
docker-compose up --build
```

This will:

- Start Zookeeper
- Start Kafka (broker `localhost:9092` externally, `kafka:29092` internally)
- Build and start the Spring Boot app on port `8080`

### 3. Verify the app is running

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
```

Expected response:

```json
{ "status": "UP" }
```

## API Reference

### POST /api/activity

Produce a user activity event.

**Request body:**

```json
{
  "userId": "user-123",
  "action": "CLICK",
  "resourceId": "product-42",
  "timestamp": 1718300000000
}
```

**Response:**

- Status: `202 ACCEPTED`
- Body:

```json
{
  "status": "ACCEPTED",
  "eventId": "<generated-event-id>"
}
```

### GET /api/activity/{userId}

Get all events for a specific user.

**Response:**

```json
[
  {
    "userId": "user-123",
    "action": "CLICK",
    "resourceId": "product-42",
    "timestamp": 1718300000000
  }
]
```

### GET /api/activity/stats

Get aggregated statistics.

**Response:**

```json
{
  "totalEvents": 10,
  "eventsByAction": {
    "PAGE_VIEW": 4,
    "CLICK": 3,
    "PURCHASE": 2,
    "LOGOUT": 1
  }
}
```

## Kafka Topics

- `user-activity-events` -> main topic for activity events
- `user-activity-events.DLT` -> dead-letter topic for invalid events

Invalid events (e.g., `userId` is null) are routed to the DLT and not retried.

## Error Handling

- Kafka consumer uses `DefaultErrorHandler` with `DeadLetterPublishingRecoverer`
- `IllegalArgumentException` is marked as non-retryable -> goes directly to DLT
- JSON deserialization errors are handled by Spring Kafka’s built-in mechanisms

## Configuration

### Local (application.yml)

- Spring port: `8080`
- Kafka brokers: `localhost:9092`
- Actuator endpoints: `/actuator/health`, `/actuator/info`

### Docker (application-docker.yml)

- Kafka brokers: `kafka:29092`
- Uses `spring.profiles.active=docker` when running in Docker

## Docker Compose Services

- `zookeeper` -> Kafka coordinator
- `kafka` -> Kafka broker
- `app` -> Spring Boot app (port `8080`)

Healthcheck:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
```

## Testing Example

```powershell
$body = @{
    userId = "user-123"
    action = "CLICK"
    resourceId = "product-42"
    timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/activity" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

Then get events:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/activity/user-123" -Method Get
```

And stats:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/activity/stats" -Method Get
```

## Troubleshooting

- **App not reachable on localhost:8080**:
  - Run `docker-compose ps` to check services
  - Run `docker-compose logs app` to see errors
- **Compilation errors**:
  - Ensure `pom.xml` includes Jackson dependencies (`jackson-core`, `jackson-databind`)
  - Run `mvn clean install -U` to refresh dependencies
- **BOM characters in Java files**:
  - Use UTF-8 without BOM when writing files in PowerShell:

```powershell
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText("src/.../YourFile.java", @'
...
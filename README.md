# Voyagrr

Voyagrr is a microservice-based travel photo and video sharing platform built with Spring Boot.  
It allows users to upload photos and videos, automatically encode videos into adaptive HLS format, securely store media, and share content with other users.

---

## Overview

Voyagrr is designed using a cloud-native microservice architecture with scalability and modularity in mind.

Core features:

- User authentication and authorization
- Photo uploads
- Video uploads with automatic HLS encoding
- Object storage integration
- Service discovery
- API gateway routing
- gRPC-based inter-service communication
- Kafka message broker
- Docker-based local development setup

## Architecture

```
                          Client
                            │
                            ▼
                        API Gateway
                            │
                            ▼
      ------------------------------------------------
      │         │        │        │         │        │
    User     Storage  Metadata  Encoding  Analysis  Future
    Service  Service  Service   Services  Service   Services
                │                 │         │
                ▼                 │         │
              MinIO ◀------------------------
```

## Services

### discovery-service
- Eureka Server
- Handles service registration and discovery

### api-gateway
- Spring Cloud Gateway (WebFlux)
- JWT validation
- Centralized routing
- Rate limiting

### analysis-service
- Performs AI-based media analysis
- Detects faces, emotions, and scene information
- Generates analysis metadata for media files
- Consumes processing events
- Produces analysis results for storage or search

### user-service
- Keycloak integration
- User management

### storage-service
- Handles file uploads
- Stores media in MinIO
- Generates signed URLs
- Manages sharing logic

### metadata-service
- Consumes metadata processing event
- Extracts metadata of file
- Produces metadata processed event

### processing-service
- Produces metadata processing event
- Consumes metadata processed event
- Stores metadata in PostgreSQL

### encoding-service
- Uses FFmpeg for video transcoding
- Generates HLS (.m3u8 + .ts segments)
- Sends encoding progress updates

### common-lib
- Shared gRPC proto files
- Shared DTOs and utilities

---

## Tech Stack

Backend:
- Java 21
- Spring Boot 3+
- Python
- gRPC
- Maven
- TensorFlow / OpenCV

Storage:
- MinIO (S3-compatible object storage)
- PostgreSQL

Authentication:
- Keycloak (OIDC / OAuth2)

Video Processing:
- FFmpeg (HLS encoding)

Infrastructure:
- Docker
- Docker Compose
- Redis (rate limiting)

## Project Structure

```
voyagrr/
├── api-gateway/
├── analysis-service/
├── common-lib/
├── discovery-service/
├── encoding-service/
├── metadata-service/
├── processing-service/
├── storage-service/
├── user-service/
├── docker-compose.yml
├── pom.xml
├── README.md
└── run.sh
```

## Running Locally

### 1. Clone the Repository

git clone https://github.com/jy-ptl/voyagrr.git
cd voyagrr

### 2. Install required dependencies
```
maven(mvn), java(open-jdk-21), docker, docker compose
```

### 3. Start All Services
```
./run.sh full build logs
```
This will start:

- PostgreSQL
- MinIO
- Keycloak
- Redis
- Eureka
- All microservices

---

## Service URLs (Default)

API Gateway: http://localhost:8080  
Eureka Dashboard: http://localhost:8761  
Keycloak: http://localhost:8081  
MinIO Console: http://localhost:9001

---

## Authentication

- Keycloak manages authentication and authorization.
- Users receive JWT tokens.
- API Gateway validates tokens.
- Downstream services verify permissions.

---

## Video Processing Flow

1. User uploads a video to storage-service.
2. Metadata is stored in PostgreSQL.
3. Processing service calls encoding-service.
4. FFmpeg generates multiple HLS resolutions.
5. Encoded files are stored in MinIO.

---

## Testing

Run all tests:

mvn clean test

---

## Future Enhancements

- Notification service
- Messaging system
- Group-based sharing
- Location-based discovery
- Trip expense tracking
- Kubernetes deployment
- Mobile application

---

## License

This project is dual-licensed:

- GNU Affero General Public License v3 (AGPLv3) for open-source use
- Commercial license for proprietary or commercial use

For commercial licensing, contact the project owner.

---

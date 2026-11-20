# Software Architecture Document (SAD)

## 1. Purpose

The purpose of this document is to describe the software architecture and technical design of Voyagrr.

It defines the major system components, their responsibilities, interactions, technology choices, and architectural
decisions.

This document serves as a technical reference for developers and contributors, guiding the design, implementation,
testing, deployment, and future evolution of the platform.

The architecture described in this document is based on the requirements and scope defined in the Voyagrr Product
Requirements Document (PRD).

## 2. Architecture Goals and Design Principles

The architecture of Voyagrr is guided by the following goals and design principles:

1. **Modularity:** Organize the system into clearly defined components with well-separated responsibilities.
2. **Maintainability:** Keep the architecture and codebase understandable, testable, and easy to maintain.
3. **Scalability:** Support growth in the number of users and media files without requiring major architectural changes.
4. **Security and Privacy:** Protect user accounts, personal media, and data through appropriate authentication,
   authorization, and access controls.
5. **Reliability:** Ensure that the system handles failures, background processing, retries, and recovery safely.
6. **Self-Hosting:** Make Voyagrr straightforward to deploy, configure, operate, and maintain on a user's own server or
   VPS.

## 3. High-Level System Architecture

### 3.1 Architecture Overview

Voyagrr follows a modular monolith architecture with a separate Python-based media processing worker.

The system consists of the following major components:

1. **Flutter Android Application:** Provides the user interface for authentication, media uploads, library browsing,
   albums, sharing, and account settings.
2. **Spring Boot Backend:** Acts as the central application layer. It handles authentication, authorization, business
   logic, REST APIs, media management, sharing, administration, and job management.
3. **PostgreSQL Database:** Stores application data, user information, media metadata, sharing information,
   configuration, and background job records. It also serves as the durable job queue.
4. **Python Media Processing Worker:** Processes media files in the background, including metadata extraction, thumbnail
   generation, image processing, and video transcoding.
5. **MinIO / S3-Compatible Object Storage:** Stores original media files and generated assets such as thumbnails,
   previews, and HLS video files.

### 3.2 Component Communication

The major system components communicate through the following interfaces:

- The Flutter Android application communicates with the Spring Boot backend through secure REST APIs over HTTPS.
- The Spring Boot backend communicates with PostgreSQL for application data and job management.
- The Python worker communicates with the Spring Boot backend through an internal HTTP API to claim jobs, report
  progress, and submit processing results.
- The Python worker does not connect directly to PostgreSQL. The backend remains responsible for managing job records
  and queue state.
- The backend and Python worker access MinIO or the configured S3-compatible storage provider for media files and
  generated assets.

### 3.3 Background Processing Flow

Background media processing follows this general workflow:

1. The backend creates a processing job and stores it in the PostgreSQL job queue.
2. The Python worker requests an available job through the internal HTTP API.
3. The backend assigns the job and provides the required processing information.
4. The worker retrieves the required media, processes it, and stores generated assets in object storage.
5. The worker reports processing progress and results through the internal HTTP API.
6. The backend updates the job status in PostgreSQL.
7. If a worker fails or its job lease expires, the backend can recover the job according to the retry and recovery
   policy.

Job leases and attempt tokens are used to manage worker ownership and prevent expired processing attempts from
incorrectly updating newer attempts.

### 3.4 Storage Architecture

Voyagrr uses an installation-wide MinIO or S3-compatible object storage provider.

Original media files and generated assets are stored in object storage, while PostgreSQL stores their metadata and
storage references.

The application uses a provider-independent storage abstraction so that the storage implementation can support
compatible providers without changing core media management logic.

## 4. Backend Architecture and Module Boundaries

### 4.1 Backend Architecture

Voyagrr uses a Spring Boot modular monolith architecture. The backend is deployed as a single application but internally
organized into independent modules with clearly defined responsibilities.

Each module manages a specific area of business functionality and communicates with other modules through well-defined
interfaces.

This approach keeps the application maintainable and extensible without introducing the operational complexity of
microservices.

### 4.2 Backend Modules

The backend is organized into the following logical modules:

| Module            | Responsibility                                                         |
|-------------------|------------------------------------------------------------------------|
| Identity & Access | User accounts, authentication, sessions, roles, and permissions.       |
| Media             | Media records, metadata, library browsing, and downloads.              |
| Upload            | Upload initiation, progress tracking, completion, and file validation. |
| Album             | Album creation, management, and media organization.                    |
| Sharing           | Internal and external sharing, share links, and permissions.           |
| Storage           | Communication with MinIO and other S3-compatible storage providers.    |
| Processing        | Background job creation, status tracking, and worker coordination.     |
| Administration    | System configuration, user administration, and system monitoring.      |
| Notification      | In-app notifications and read/unread status.                           |
| Audit             | Security events and administrative activity records.                   |

These modules are logical boundaries within one backend application and are not separate deployable services.

### 4.3 Module Design Principles

Each backend module follows these design principles:

- Maintain clear ownership of its business responsibilities.
- Keep internal implementation details separate from other modules.
- Expose well-defined interfaces for communication with other modules.
- Avoid directly modifying another module's internal data.
- Maintain data consistency through controlled application boundaries and database transactions.
- Minimize unnecessary dependencies between modules.

## 5. Database and Storage Architecture

### 5.1 Database Architecture

Voyagrr uses PostgreSQL as its primary relational database for storing and managing application data.

The database stores the following information:

- User accounts, authentication, and session information.
- Media records and extracted metadata.
- Albums and media relationships.
- Sharing permissions and share links.
- Storage references and usage information.
- Background job records and processing status.
- Notifications and audit logs.
- System configuration.

PostgreSQL also serves as the durable background job queue, allowing the backend to manage job scheduling, assignment,
status tracking, retries, and recovery.

Database schema changes are managed using Liquibase migrations.

### 5.2 Storage Architecture

Voyagrr uses MinIO or another compatible S3 object storage provider to store original media files and generated media
assets.

The system uses a single storage provider for the entire installation. Per-user storage provider configuration and local
filesystem storage are not supported in the MVP.

The following types of files are stored in object storage:

- Original uploaded photos and videos.
- Generated thumbnails.
- Optimized previews.
- HLS playlists and video segments generated during video processing.

PostgreSQL stores media metadata and references to the corresponding objects in storage. The actual media files are
stored in the configured object storage provider.

A provider-independent storage abstraction is used to keep the application logic separate from the underlying storage
implementation.

The Spring Boot backend accesses object storage using the MinIO Java SDK, while the Python media processing worker uses
a compatible Python S3 client.

Both components access the same configured storage provider and follow the application's storage conventions for
managing original media files and generated assets.

### 5.3 Data Consistency Between Database and Object Storage

PostgreSQL and object storage operate as separate systems. The application must handle partial failures to maintain
consistency between media records and stored files.

The architecture follows these principles:

- Media records and their storage references must be managed consistently.
- Upload and processing workflows must handle failures without creating invalid or incomplete media records.
- Failed or interrupted operations must support safe recovery or cleanup.
- Media deletion must remove the corresponding original files and generated assets according to the deletion policy.
- Unreferenced storage objects must be identifiable and eligible for cleanup.
- Database records must not reference missing storage objects after successful completion of an operation.

## 6. Background Processing Architecture

### 6.1 Processing Architecture

Voyagrr uses a separate Python-based worker for background media processing. The Spring Boot backend manages job
creation, scheduling, assignment, and status tracking, while the Python worker performs the actual media processing
tasks.

PostgreSQL acts as the durable job queue. The Python worker communicates with the backend through an internal HTTP API
to claim jobs, report progress, and submit processing results. The worker does not connect directly to PostgreSQL.

### 6.2 Worker Authentication and Communication Security

The Python media processing worker communicates with the Spring Boot backend through authenticated internal HTTP API
requests.

Worker authentication uses a dedicated service identity separate from regular user accounts.

The backend validates worker credentials and authorizes requests before allowing job claiming, progress reporting, or
processing result submission.

Worker credentials must be securely managed and must not be hardcoded or exposed in application logs.

Internal worker communication must be protected against unauthorized access and misuse.

### 6.3 Processing Responsibilities

The Python worker is responsible for:

- Extracting image and video metadata.
- Generating image thumbnails and previews.
- Generating video thumbnails and previews.
- Checking video compatibility and transcoding to HLS when required.
- Storing generated media assets in MinIO or S3-compatible storage.
- Reporting processing progress, completion, and failures to the backend.

### 6.4 Job Reliability and Recovery

The background processing system supports reliable job execution through:

- Job leases and heartbeats to track active worker assignments.
- Attempt tokens to prevent expired processing attempts from updating newer attempts.
- Automatic retries for temporary failures.
- Configurable maximum retry attempts.
- Exponential backoff with jitter between retry attempts.
- Recovery of jobs when a worker crashes or its lease expires.
- Manual retry of eligible failed jobs.
- Idempotent processing to prevent duplicate or inconsistent results.

The backend remains responsible for job state management, retry scheduling, and recovery.

## 7. API and Client Architecture

### 7.1 API Architecture

Voyagrr follows an API-first architecture in which the backend exposes REST APIs independently of the client
applications.

The Spring Boot backend provides the APIs required for authentication, user management, media uploads and downloads,
media library operations, albums, sharing, notifications, and administration.

The APIs are designed to support multiple client applications through a consistent interface.

OpenAPI is used to document API endpoints, request and response formats, authentication requirements, and error
responses.

### 7.2 Client Architecture

The initial client application is developed using Flutter for Android.

The Android application communicates with the Spring Boot backend through secure REST APIs over HTTPS.

The client application is responsible for:

- User authentication and session handling.
- Media library browsing and viewing.
- Photo and video uploads with progress tracking.
- Album management.
- Media sharing.
- Media downloads.
- Notifications and application settings.

The Flutter application follows a feature-based architecture using Riverpod for state management.

### 7.3 Future Client Support

The backend API is designed to support future client applications without requiring major changes to the core backend
architecture.

Planned future clients include:

- iOS application.
- Web application.
- Desktop application.

## 8. Deployment Architecture

### 8.1 Deployment Overview

Voyagrr uses Docker Compose as its primary deployment method. The platform is designed to run on a self-hosted VPS or
server managed by the user.

The deployment includes the following application services:

- Spring Boot backend.
- Python media processing worker.
- PostgreSQL database.
- MinIO object storage, when self-hosted storage is selected.

Users may alternatively configure an external S3-compatible object storage provider. In this case, the external storage
service operates independently of the Voyagrr Docker Compose deployment.

Each self-hosted application service runs in its own container and is managed through Docker Compose.

### 8.2 Deployment Principles

The deployment architecture follows these principles:

- Use Docker Compose to manage application services.
- Run application components in separate containers.
- Maintain persistent data across container restarts and application updates.
- Manage application configuration through environment variables or configuration files.
- Protect credentials and sensitive configuration values.
- Use health checks to monitor service availability.
- Provide documented procedures for installation, updates, backups, and restoration.

The deployment must allow users to configure server resources according to their infrastructure capacity.

## 9. Security and Operational Considerations

### 9.1 Authentication and Authorization

Voyagrr uses secure authentication and authorization mechanisms to protect user accounts and application resources.

The security architecture includes:

- JWT-based authentication using short-lived access tokens.
- Rotating refresh tokens.
- Role-based access control.
- Ownership-based media access.
- Explicit sharing permissions.
- Session management and revocation.

### 9.2 Data Privacy and Protection

Voyagrr protects user privacy through access control and secure data handling.

- Users can access their own media and media explicitly shared with them.
- Private media must not be accessible to other users without authorization.
- System administrators cannot browse, download, or modify users' private media through the application.
- Credentials and sensitive configuration values must be protected.
- External communication must use HTTPS.

Infrastructure-level access by the server owner or administrators with direct database or object-storage access is
outside the application-level administrator restrictions.

### 9.3 Logging and Auditing

The system maintains logs and audit records for operational visibility and security.

- Record important application and security events.
- Maintain administrative activity records.
- Record background job failures and processing errors.
- Prevent passwords, access tokens, and storage credentials from appearing in logs.
- Protect audit records against unauthorized modification or deletion.

### 9.4 Monitoring and Maintenance

Voyagrr provides operational monitoring and maintenance capabilities.

- Monitor backend, worker, database, and storage health.
- Track background job status and failures.
- Monitor server resource utilization.
- Support database and media backups.
- Provide documented recovery, maintenance, and update procedures.

## 10. Architecture Decisions and Trade-offs

### 10.1 Architecture Decisions

The following table summarizes the major technical decisions for Voyagrr and their current approval status.

| Area                        | Decision                                      | Status   |
|-----------------------------|-----------------------------------------------|----------|
| Backend Architecture        | Spring Boot Modular Monolith                  | Accepted |
| Media Processing            | Separate Python Worker                        | Accepted |
| Primary Database            | PostgreSQL                                    | Accepted |
| Background Job Queue        | PostgreSQL Durable Job Queue                  | Accepted |
| Database Migrations         | Liquibase                                     | Accepted |
| Storage Client              | MinIO Java SDK for the backend                | Accepted |
| Storage Architecture        | S3-Compatible Storage Abstraction             | Accepted |
| Storage Configuration       | One Provider Per Installation                 | Accepted |
| Authentication              | JWT Access Tokens and Rotating Refresh Tokens | Proposed |
| Media Access Control        | Ownership and Explicit Sharing Grants         | Accepted |
| Upload Strategy             | Presigned Direct and Multipart Uploads        | Proposed |
| Metadata Storage            | Relational Data and JSONB                     | Accepted |
| Video Playback              | Hybrid Original and HLS Playback              | Accepted |
| Android Application         | Flutter with Riverpod                         | Proposed |
| Deployment                  | Docker Compose                                | Accepted |
| API Documentation           | OpenAPI-First                                 | Accepted |
| Repository Structure        | Monorepo                                      | Accepted |
| Backend Module Organization | Spring Modulith                               | Proposed |
| Geospatial Database Support | PostGIS                                       | Proposed |
| Java Compatibility          | Java 21+ baseline; Java 25 LTS target         | Proposed |

Redis, Kafka, and RabbitMQ are excluded from the MVP.

Proposed decisions require further validation or confirmation before implementation.

### 10.2 Architecture Trade-offs

The selected architecture involves the following trade-offs:

- **Modular Monolith:** Simplifies deployment and operations compared to microservices, but requires clear module
  boundaries.
- **PostgreSQL Job Queue:** Reduces infrastructure complexity, but PostgreSQL must handle both application data and
  background job workloads.
- **Separate Python Worker:** Enables specialized media-processing capabilities, but introduces an additional service to
  deploy and monitor.
- **S3-Compatible Storage:** Provides storage flexibility, but compatibility must be validated across providers.
- **Docker Compose:** Simplifies self-hosted deployment, but does not provide large-scale container orchestration.
- **Flutter:** Supports a shared codebase for future client platforms, but some platform-specific features may require
  native integrations.
# Voyagrr — Project Overview

## 1. Introduction

Voyagrr is an open-source, self-hosted personal media management
platform designed to help users upload, store, organize, browse and
share their personal photos and videos.

It provides users with control over their media library and the
infrastructure on which their media is stored.

Voyagrr is designed to support individual users, families and
multi-user installations through a flexible self-hosted deployment.

## 2. Vision

The vision of Voyagrr is to provide a centralized, private and
user-controlled platform for managing personal photos and videos.

Users should be able to host their own media infrastructure,
maintain control over their data, access their media across
supported devices and selectively share content with others.

In the long term, Voyagrr aims to evolve into a comprehensive
personal media platform that can grow with users' needs while
keeping privacy, flexibility and self-hosting at its core.

## 3. Problem Statement

Many users want to manage their personal photos and videos
without depending entirely on third-party cloud platforms
or services.

Existing solutions may limit users' control over storage
infrastructure, configuration, data ownership or deployment
choices.

Voyagrr aims to address these concerns by providing:

- Self-hosting: Users can deploy and operate their own media server.
- Data control: Users retain control over their media and
  storage infrastructure.
- Flexible configuration: Users can configure the platform
  according to their infrastructure and requirements.
- Reduced external dependency: Core media management
  functionality should not require a mandatory third-party
  hosted platform.
- Simple deployment: Installing, configuring, maintaining
  and upgrading the platform should be straightforward.
- Controlled sharing: Users can selectively share their media
  without surrendering control of their entire library.

## 4. Target Users

Voyagrr is designed for the following user groups:

- Individual users who want to manage and preserve their
  personal photos and videos on infrastructure they control.
- Families and households who want to manage their personal
  media libraries within a shared installation while keeping
  each user's library private.
- Self-hosting enthusiasts who prefer managing their own
  infrastructure, storage and application configuration.

Each user has a private media library and can selectively share
albums or individual media with other users or external recipients.

## 5. Core Features

Voyagrr aims to provide the following capabilities over time.

### 5.1 Media Management

- Upload and download photos and videos.
- Organize and browse personal media libraries.
- Browse media through a timeline.
- Create and manage albums.
- Detect exact duplicate files.
- Support trash, restore and permanent deletion workflows.

### 5.2 Sharing

- Share selected media and albums with other Voyagrr users.
- Generate external share links for recipients without
  Voyagrr accounts.
- Support configurable expiry, access permissions,
  password protection and revocation for external shares.

### 5.3 Media Processing

- Automatically extract metadata from supported images
  and videos.
- Generate thumbnails and previews.
- Transcode videos into HLS or other suitable streaming
  formats when required.
- Support playback of original videos when compatible.

### 5.4 Device Synchronization

- Automatically synchronize selected Android device folders
  with Voyagrr.

### 5.5 Location and Discovery

- Extract GPS coordinates from supported media.
- Store location information alongside media metadata.
- Display geotagged photos and videos on an interactive map
  in a future release.

## 6. Technology Overview

Voyagrr will use the following technology stack:

- Backend: Java with Spring Boot, implemented as a modular
  monolith.
- Mobile application: Flutter, with Android as the initial
  target platform.
- Media processing worker: Python.
- Database: PostgreSQL for application data and durable
  background job management.
- Object storage: MinIO and other S3-compatible storage
  providers through a provider-independent abstraction.
- Media processing: FFmpeg for video processing and transcoding.
- Database migrations: Liquibase.
- Deployment: Docker Compose for self-hosted installations.

The architecture will be API-first to support additional
client platforms in the future.

## 7. MVP Scope

The Minimum Viable Product (MVP) will focus on delivering
a reliable, self-hosted personal media management platform.

### 7.1 Included in MVP

#### User and Access Management

- User authentication and account management.
- Configurable registration policy.
- Private media libraries with ownership-based access control.
- Multi-user support within a single installation.

#### Media Management

- Upload and download photos and videos.
- Resumable or multipart uploads for larger files.
- Exact duplicate detection using file hashes.
- Media timeline and basic browsing.
- Basic filtering and search.
- Basic album management.
- Trash, restore and permanent deletion workflows.

#### Sharing

- Internal sharing between Voyagrr users.
- External share links for recipients without Voyagrr accounts.
- Expiry and revocation of external share links.
- Appropriate access permissions for shared content.
- Optional password protection for external shares.

#### Media Processing

- Automatic extraction of supported image and video metadata.
- GPS metadata extraction and storage where available.
- Thumbnail and preview generation.
- Hybrid video playback:
    - Original video playback when compatible.
    - HLS transcoding when required.

#### Storage and Deployment

- S3-compatible object storage.
- One storage provider configured per installation.
- Docker Compose-based self-hosted deployment.
- Basic operational monitoring and backup procedures.

#### Administration

- System-level administration for managing users,
  configuration, background jobs and storage statistics.
- No application-level administrator capability to browse
  or download users' private media.

### 7.2 Excluded from MVP

The following capabilities are intentionally excluded
from the initial release:

- Automatic Android folder synchronization.
- iOS, web and desktop applications.
- Interactive map-based media exploration.
- Trips and travel timelines.
- Smart albums.
- Social networking features.
- Messaging and calling.
- AI-powered media organization and recognition.
- Advanced media discovery.
- Microservices architecture.

These capabilities may be considered for future releases.

## 8. Future Features

The following capabilities represent the long-term vision
for Voyagrr. They are not commitments for the initial MVP.

### 8.1 Additional Platforms

- iOS application.
- Web application.
- Desktop applications.

### 8.2 Advanced Media Management

- Automatic Android folder synchronization.
- Smart albums and advanced media organization.
- Interactive map-based media exploration.
- Trips and travel timelines.
- Advanced media search and discovery.

### 8.3 Social and Communication

- Social features and shared experiences.
- Messaging.
- Audio and video calling.

### 8.4 Intelligent Features

- AI-powered media organization.
- Facial recognition and people-based grouping.
- Intelligent media search and discovery.

## 9. Product Principles

Voyagrr will be guided by the following principles:

- Self-hosting first: Users should be able to operate
  Voyagrr on infrastructure they control.
- Privacy by default: Personal media libraries should
  remain private unless explicitly shared.
- User control: Users should retain control over their
  media, storage and sharing decisions.
- Flexible deployment: The platform should support
  different self-hosted infrastructure configurations.
- Extensibility: The architecture should support future
  clients and capabilities without unnecessary complexity.
- Simplicity: Installation, configuration and maintenance
  should be straightforward.

---

*This document describes the product direction and high-level
scope of Voyagrr. Detailed functional requirements, technical
architecture, API contracts and implementation plans are
maintained in separate documents.*
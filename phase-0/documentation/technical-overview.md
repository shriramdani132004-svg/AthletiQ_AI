# AthletiQ Technical Overview

## Logical Architecture

React Frontend
→ Spring Boot REST APIs
→ PostgreSQL

Spring Boot
→ Redis / Background Queue
→ Python FastAPI AI Service

Spring Boot
→ WebSocket / SSE
→ Organizer Dashboard

Spring Boot
→ Transactional Email

Future:
→ Object Storage
→ Docker
→ CI/CD
→ Monitoring

## Service Responsibilities

### Frontend
- User interface.
- Forms.
- Dashboards.
- Candidate views.
- Real-time event consumption.

### Backend
- Authentication.
- Authorization.
- Business rules.
- Events.
- Forms.
- Applications.
- Evaluation orchestration.
- Ranking.
- Selection.
- Communication.
- REST API.

### AI Service
- AI-assisted candidate analysis.
- Strength/weakness extraction.
- Recommendation.
- Explanation.

### PostgreSQL
Persistent transactional state.

### Redis
Caching and background queue infrastructure.

## Data Flow

Player
→ Public Application
→ Backend
→ Validation
→ PostgreSQL
→ Evaluation Queue
→ Objective Evaluation
→ AI Service
→ Ranking
→ PostgreSQL
→ Real-Time Event
→ Organizer Dashboard

## Principles

- Expensive processing is asynchronous.
- Transactional state is persisted before notification.
- Authorization is server-side.
- Database is source of truth.

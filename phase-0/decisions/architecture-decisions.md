# AthletiQ Architecture Decisions

## AD-001 Monorepo

One repository contains:
- React frontend.
- Spring Boot backend.
- Python AI service.
- Infrastructure.
- Documentation.

Reason: keeps the complete project together while maintaining service boundaries.

## AD-002 Frontend

React.js.

Reason: suitable component-based frontend architecture.

## AD-003 Backend

Spring Boot REST API.

Reason: primary transactional backend.

## AD-004 AI Service

Python + FastAPI.

Reason: isolates AI workloads from transactional backend.

## AD-005 Database

PostgreSQL.

Reason: relational model fits events, forms, applications, evaluation and selection state.

## AD-006 Cache / Queue

Redis.

Reason: supports caching and background processing.

## AD-007 Real-Time Transport

WebSocket or Server-Sent Events.

Reason: live dashboard updates.

## AD-008 AI Processing

Asynchronous queue-based processing.

Reason: prevents expensive AI work from blocking HTTP requests.

## AD-009 Objective vs AI Evaluation

Objective evaluation remains logically separate from AI-assisted evaluation.

Reason: deterministic scoring and assisted analysis have different responsibilities.

## AD-010 Security Boundary

Backend enforces:

Authentication
→ Role
→ Permission
→ Resource Ownership / Access

## AD-011 Database Source of Truth

Persist state before broadcasting real-time notifications.

## AD-012 Future Deployment

Docker + CI/CD + production monitoring.

Reason: supports reproducible deployment and operational visibility.

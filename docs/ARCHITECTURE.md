# AthletiQ Architecture

## High-Level Architecture

React / Vite -> Spring Boot REST API -> PostgreSQL

Spring Boot -> Redis
Spring Boot -> FastAPI AI Service

## Backend Layers

Controller -> DTO -> Service -> Repository -> PostgreSQL

## Principles

Separation of concerns
API versioning
DTO-based contracts
Centralized exception handling
Environment-based configuration
Automated testing
CI validation
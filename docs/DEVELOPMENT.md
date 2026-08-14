# AthletiQ Development Guide

## Prerequisites

Windows 11 / Linux
Java 26
Maven 3.9+
Node.js 24+
Python 3.14+
PostgreSQL 18+
Docker Desktop
Git

## Services

Frontend: 5173
Spring Boot: 8080
FastAPI AI: 8000
PostgreSQL: 5432
Redis: 6379

## Frontend

    cd frontend
    npm install
    npm run dev

## Backend

    cd backend
    .\mvnw.cmd spring-boot:run

## AI Service

    cd ai-service
    .\.venv\Scripts\Activate.ps1
    python -m uvicorn app:app --host 127.0.0.1 --port 8000

## URLs

Frontend: http://127.0.0.1:5173
Backend: http://127.0.0.1:8080
AI: http://127.0.0.1:8000/health
# AthletiQ Frontend Authentication

## Authentication Flow

Login ↓	Spring Boot Authentication API ▓
Access Token + Refresh Token ▓
AuthContext ↓ProtectedRoute ↓Authenticated Application

## Token Storage

Development uses browser localStorage for tokens. Production should use secure HttpOnly refresh cookies when hardening the authentication system.

## Protected Routes

Unauthenticated users are redirected to `/login`.

## API Authorization

Authorization: Bearer <access-token>

## Logout

Logout revokes the refresh session through the backend.

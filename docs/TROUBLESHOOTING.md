# AthletiQ Troubleshooting

## PostgreSQL

Test-NetConnection localhost -Port 5432

## Redis

docker exec athletiq-redis redis-cli ping

Expected: PONG

## Backend

cd backend
.\mvnw.cmd clean package

## Frontend

cd frontend
npm install
npm test
npm run build

## AI Service

cd ai-service
.\.venv\Scripts\python.exe -m uvicorn app:app --host 127.0.0.1 --port 8000
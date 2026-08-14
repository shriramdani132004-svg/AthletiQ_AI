from fastapi import FastAPI

app = FastAPI(
    title="AthletiQ AI Service",
    version="0.1.0"
)

@app.get("/health")
def health():
    return {
        "service": "athletiq-ai",
        "status": "UP"
    }

@app.get("/")
def root():
    return {
        "service": "AthletiQ AI Service",
        "message": "AI service is running"
    }

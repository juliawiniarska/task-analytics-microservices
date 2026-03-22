from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

# This defines what data you expect from the Java side
class Task(BaseModel):
    id: int
    title: str
    priority: int
    estimated_hours: float

@app.post("/analyze-task")
async def analyze_task(task: Task):
    # This is where your analytical logic will eventually go
    complexity_score = task.priority * task.estimated_hours
    
    return {
        "task_id": task.id,
        "complexity_score": complexity_score,
        "recommendation": "High Priority" if complexity_score > 10 else "Standard"
    }

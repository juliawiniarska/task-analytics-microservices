from fastapi import FastAPI
from .models import Task, AnalysisResult
from .logic import perform_task_analysis

app = FastAPI(title="Task Analytics Service")

@app.get("/")
def home():
    return {"message": "Analytics Service is Online"}

@app.post("/analyze", response_model=AnalysisResult)
async def analyze(task: Task):
    result = perform_task_analysis(task)
    return result

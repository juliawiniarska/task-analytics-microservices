from typing import List
from fastapi import FastAPI
from .models import Task, AnalysisResult, PlanProgressResult
from .logic import perform_task_analysis, calculate_plan_progress

app = FastAPI(title="Task Analytics Service")

@app.get("/")
def home():
    return {"message": "Analytics Service is Online"}

@app.post("/analyze", response_model=AnalysisResult)
async def analyze(task: Task):
    result = perform_task_analysis(task)
    return result


@app.post("/analyze-plan/{plan_id}", response_model=PlanProgressResult)
async def analyze_plan(plan_id: int, tasks: List[Task]):
    result = calculate_plan_progress(plan_id, tasks)
    return result
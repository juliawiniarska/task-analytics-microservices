from typing import List
from fastapi import FastAPI
from .models import Task, AnalysisResult, PlanProgressResult
from .logic import perform_task_analysis, calculate_plan_progress
from .database import engine, get_db
from . import db_models
from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from typing import List

db_models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="Task Analytics Service")

db_models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="Task Analytics Service")
app = FastAPI(title="Task Analytics Service")

@app.get("/")
def home():
    return {"message": "Analytics Service is Online"}

@app.post("/analyze", response_model=AnalysisResult)
async def analyze(task: Task):
    result = perform_task_analysis(task)
    return result


@app.get("/analyze-plan/{plan_id}", response_model=PlanProgressResult)
def analyze_plan(plan_id: int, db: Session = Depends(get_db)):
    db_tasks = db.query(db_models.DBTask).filter(db_models.DBTask.plan_id == plan_id).all()
    
    python_tasks = []
    for t in db_tasks:
        python_tasks.append(
            Task(
                id=t.id,
                plan_id=t.plan_id,
                title=t.title,
                priority=t.priority,
                estimated_hours=t.estimated_hours,
                status=t.status,
                due_date=t.due_date
            )
        )
        
    result = calculate_plan_progress(plan_id, python_tasks)
    return result
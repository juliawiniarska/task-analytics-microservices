from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class Task(BaseModel):
    id: int
    plan_id: int
    title: str
    priority: int
    estimated_hours: float
    status: str               
    due_date: Optional[datetime] = None

class AnalysisResult(BaseModel):
    task_id: int
    smart_score: float
    risk_level: str
    burnout_warning: bool

class PlanProgressResult(BaseModel):
    plan_id: int
    total_tasks: int
    completed_tasks: int
    completion_percentage: float
    total_hours: float
    remaining_hours: float
    plan_status: str
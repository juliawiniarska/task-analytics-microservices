from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class Task(BaseModel):
    id: int
    title: str
    description: Optional[str] = None
    priority: int  # 1 (low) to 5 (high)
    estimated_hours: float
    created_at: datetime = datetime.now()

class AnalysisResult(BaseModel):
    task_id: int
    complexity_score: float
    risk_level: str
    suggested_deadline: datetime

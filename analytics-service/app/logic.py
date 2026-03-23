from .models import Task, AnalysisResult
from datetime import timedelta

def perform_task_analysis(task: Task) -> AnalysisResult:
    """
    The core analytical logic. 
    In the future, this could use Machine Learning or complex stats.
    """
    # Simple logic: higher priority and more hours = higher complexity
    score = (task.priority * 1.5) + (task.estimated_hours * 0.5)
    
    # Determine risk
    if score > 15:
        risk = "Critical"
    elif score > 8:
        risk = "Moderate"
    else:
        risk = "Low"

    # Suggest a deadline (e.g., 2 days per estimated 8 hours)
    days_needed = max(1, round(task.estimated_hours / 4))
    deadline = task.created_at + timedelta(days=days_needed)

    return AnalysisResult(
        task_id=task.id,
        complexity_score=round(score, 2),
        risk_level=risk,
        suggested_deadline=deadline
    )

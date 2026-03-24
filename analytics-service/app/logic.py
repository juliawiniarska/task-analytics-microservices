from .models import Task, AnalysisResult
from datetime import timedelta

def perform_task_analysis(task: Task) -> AnalysisResult:
    score = (task.priority * 1.5) + (task.estimated_hours * 0.5)
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

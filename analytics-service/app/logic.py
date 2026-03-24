from .models import Task, AnalysisResult
from datetime import datetime

def perform_task_analysis(task: Task) -> AnalysisResult:

    base_score = task.priority * 10
    effort_weight = task.estimated_hours * 0.5
    
    urgency_multiplier = 1.0
    if task.due_date:
        days_remaining = (task.due_date.replace(tzinfo=None) - datetime.now()).days
        
        if days_remaining <= 0:
            urgency_multiplier = 3.0
        elif days_remaining <= 3:
            urgency_multiplier = 2.0
        elif days_remaining <= 7:
            urgency_multiplier = 1.5

    smart_score = (base_score + effort_weight) * urgency_multiplier

    if smart_score >= 80:
        risk = "Critical - Do Immediately"
    elif smart_score >= 50:
        risk = "High - Plan for this week"
    elif smart_score >= 30:
        risk = "Medium - Monitor"
    else:
        risk = "Low - Backlog"

    burnout = False
    if task.due_date:
        days_remaining = max(1, (task.due_date.replace(tzinfo=None) - datetime.now()).days)
        hours_per_day = task.estimated_hours / days_remaining
        
        if hours_per_day > 6:
            burnout = True

    return AnalysisResult(
        task_id=task.id,
        smart_score=round(smart_score, 2),
        risk_level=risk,
        burnout_warning=burnout
    )

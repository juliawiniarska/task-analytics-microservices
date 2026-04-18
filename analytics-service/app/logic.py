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

from typing import List

def calculate_plan_progress(plan_id: int, tasks: List[Task]) -> PlanProgressResult:
    total_tasks = len(tasks)
    
    if total_tasks == 0:
        return PlanProgressResult(
            plan_id=plan_id, total_tasks=0, completed_tasks=0, 
            completion_percentage=0.0, total_hours=0.0, 
            remaining_hours=0.0, plan_status="Empty"
        )

    completed_tasks = sum(1 for t in tasks if t.status == "done")
    total_hours = sum(t.estimated_hours for t in tasks)
    completed_hours = sum(t.estimated_hours for t in tasks if t.status == "done")
    remaining_hours = total_hours - completed_hours

    completion_percentage = (completed_hours / total_hours) * 100

    if completion_percentage == 100:
        health = "Completed"
    elif remaining_hours > 40 and completed_tasks == 0:
        health = "At Risk - Not Started"
    else:
        health = "In Progress"

    return PlanProgressResult(
        plan_id=plan_id,
        total_tasks=total_tasks,
        completed_tasks=completed_tasks,
        completion_percentage=round(completion_percentage, 1),
        total_hours=total_hours,
        remaining_hours=remaining_hours,
        plan_status=health
    )
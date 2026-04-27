# app/listener.py
import pika
import json
from datetime import datetime
from .database import SessionLocal
from .db_models import DBTask
from .models import Task
from .logic import perform_task_analysis

def map_priority(prio_str: str) -> int:
    mapping = {"LOW": 1, "MEDIUM": 3, "HIGH": 5}
    return mapping.get(prio_str, 3) # Defaults to 3 if unknown

def parse_date(date_str: str):
    if not date_str:
        return None
    clean_str = date_str.replace("Z", "")
    try:
        return datetime.fromisoformat(clean_str)
    except ValueError:
        return None

def process_event(ch, method, properties, body):
    event = json.loads(body)
    event_type = event.get("eventType")
    task_id = event.get("taskId")
    
    print(f"--> Received {event_type} for Task ID: {task_id}")
    
    db = SessionLocal()
    
    try:
        if event_type in ["TASK_CREATED", "TASK_UPDATED", "TASK_STATUS_CHANGED", "TASK_COMPLETED"]:
            python_task = Task(
                id=task_id,
                plan_id=1, # Defaulting to 1 for now
                title=event.get("title", "Unknown"),
                priority=map_priority(event.get("priority")),
                estimated_hours=event.get("estimated_hours", 10.0), # Fallback if Java doesn't send it
                status=event.get("status", "TODO"),
                due_date=parse_date(event.get("dueDate"))
            )
            
            analysis = perform_task_analysis(python_task)
            
            db_task = db.query(DBTask).filter(DBTask.id == task_id).first()
            if not db_task:
                db_task = DBTask(id=task_id)
                db.add(db_task)
            
            db_task.title = python_task.title
            db_task.priority = python_task.priority
            db_task.status = python_task.status
            db_task.due_date = python_task.due_date
            db_task.smart_score = analysis.smart_score
            db_task.risk_level = analysis.risk_level
            db_task.burnout_warning = analysis.burnout_warning
            
            db.commit()
            print(f"    Saved/Updated Task {task_id} with Smart Score: {analysis.smart_score}")

        elif event_type == "TASK_DELETED":
            db_task = db.query(DBTask).filter(DBTask.id == task_id).first()
            if db_task:
                db.delete(db_task)
                db.commit()
                print(f"    Deleted Task {task_id} from Analytics DB")
                
    except Exception as e:
        print(f"Error processing message: {e}")
        db.rollback()
    finally:
        db.close()

def start_listening():
    connection = pika.BlockingConnection(pika.ConnectionParameters('host.docker.internal'))
    channel = connection.channel()

    channel.queue_declare(queue='task.analytics.queue', durable=True)

    print(" [*] Analytics Service is waiting for messages. To exit press CTRL+C")
    
    channel.basic_consume(queue='task.analytics.queue', on_message_callback=process_event, auto_ack=True)
    channel.start_consuming()

if __name__ == "__main__":
    start_listening()
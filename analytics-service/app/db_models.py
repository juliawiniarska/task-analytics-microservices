from sqlalchemy import Column, Integer, String, Float, DateTime, Boolean
from .database import Base

class DBTask(Base):
    __tablename__ = "analytics_tasks"

    id = Column(Integer, primary_key=True, index=True)
    plan_id = Column(Integer, index=True, nullable=True)
    title = Column(String, index=True)
    priority = Column(Integer)
    estimated_hours = Column(Float)
    status = Column(String)
    due_date = Column(DateTime, nullable=True)
    
    # These are the analytical results we will calculate and store!
    smart_score = Column(Float, nullable=True)
    risk_level = Column(String, nullable=True)
    burnout_warning = Column(Boolean, default=False)
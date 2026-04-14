package com.taskapp.taskservice.event;

/**
 * Typy zdarzeń publikowanych na kolejkę.
 */
public enum TaskEventType {
    TASK_CREATED,
    TASK_UPDATED,
    TASK_DELETED,
    TASK_STATUS_CHANGED,
    TASK_COMPLETED
}
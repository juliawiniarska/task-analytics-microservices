package com.taskapp.taskservice.event;

import com.taskapp.taskservice.model.TaskPriority;
import com.taskapp.taskservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Klasa reprezentująca zdarzenie dotyczące zadania,
 * publikowane na kolejkę RabbitMQ.
 * Format komunikatu (kontrakt) uzgodniony z usługą analityczną.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEvent {

    /** Typ zdarzenia */
    private TaskEventType eventType;

    /** ID zadania którego dotyczy zdarzenie */
    private Long taskId;

    /** Tytuł zadania */
    private String title;

    /** Status zadania */
    private TaskStatus status;

    /** Poprzedni status (przy zmianie statusu) */
    private TaskStatus previousStatus;

    /** Priorytet zadania */
    private TaskPriority priority;

    /** Planowany termin realizacji */
    private LocalDate dueDate;

    /** Data utworzenia zadania */
    private LocalDateTime createdAt;

    /** Data ukończenia (null jeśli nieukończone) */
    private LocalDateTime completedAt;

    /** Znacznik czasu zdarzenia */
    private LocalDateTime eventTimestamp;
}
package com.taskapp.taskservice.event;

import com.taskapp.taskservice.config.RabbitMQConfig;
import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Komponent odpowiedzialny za publikowanie zdarzeń
 * dotyczących zadań na kolejkę RabbitMQ.
 * Wzorzec projektowy: Observer (producent zdarzeń).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publikuje zdarzenie na kolejkę RabbitMQ.
     */
    public void publish(TaskEventType eventType, Task task, TaskStatus previousStatus) {
        TaskEvent event = TaskEvent.builder()
                .eventType(eventType)
                .taskId(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .previousStatus(previousStatus)
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .eventTimestamp(LocalDateTime.now())
                .build();

        String routingKey = "task.event." + eventType.name().toLowerCase();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    event
            );
            log.info("Opublikowano zdarzenie: type={}, taskId={}, routingKey={}",
                    eventType, task.getId(), routingKey);
        } catch (Exception e) {
            log.error("Błąd publikacji zdarzenia: type={}, taskId={}, error={}",
                    eventType, task.getId(), e.getMessage());
        }
    }
}
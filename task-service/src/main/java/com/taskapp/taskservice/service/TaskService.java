package com.taskapp.taskservice.service;

import com.taskapp.taskservice.dto.ChangeStatusRequest;
import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.event.TaskEventPublisher;
import com.taskapp.taskservice.event.TaskEventType;
import com.taskapp.taskservice.exception.InvalidStatusTransitionException;
import com.taskapp.taskservice.exception.TaskNotFoundException;
import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskPriority;
import com.taskapp.taskservice.model.TaskStatus;
import com.taskapp.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Serwis realizujący logikę biznesową zarządzania zadaniami.
 * Odpowiada za operacje CRUD, zmianę statusu, oznaczanie jako ukończone
 * oraz publikację zdarzeń na kolejkę RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    /**
     * Dozwolone przejścia między statusami zadania.
     * Wzorzec: State Machine (uproszczony).
     */
    private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED_TRANSITIONS = Map.of(
            TaskStatus.TODO, Set.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED),
            TaskStatus.IN_PROGRESS, Set.of(TaskStatus.TODO, TaskStatus.DONE, TaskStatus.CANCELLED),
            TaskStatus.DONE, Set.of(),
            TaskStatus.CANCELLED, Set.of(TaskStatus.TODO)
    );

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Utworzono zadanie: id={}, tytuł='{}'", savedTask.getId(), savedTask.getTitle());

        // Publikacja zdarzenia TASK_CREATED
        taskEventPublisher.publish(TaskEventType.TASK_CREATED, savedTask, null);

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(TaskStatus status) {
        List<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            tasks = taskRepository.findAllByOrderByCreatedAtDesc();
        }
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = findTaskOrThrow(id);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Zaktualizowano zadanie: id={}", updatedTask.getId());

        // Publikacja zdarzenia TASK_UPDATED
        taskEventPublisher.publish(TaskEventType.TASK_UPDATED, updatedTask, null);

        return TaskResponse.fromEntity(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = findTaskOrThrow(id);

        // Publikacja zdarzenia TASK_DELETED przed usunięciem
        taskEventPublisher.publish(TaskEventType.TASK_DELETED, task, null);

        taskRepository.delete(task);
        log.info("Usunięto zadanie: id={}", id);
    }

    public TaskResponse changeStatus(Long id, ChangeStatusRequest request) {
        Task task = findTaskOrThrow(id);
        TaskStatus currentStatus = task.getStatus();
        TaskStatus newStatus = request.getStatus();

        Set<TaskStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        TaskStatus previousStatus = task.getStatus();
        task.setStatus(newStatus);

        if (newStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Zmieniono status zadania: id={}, {} -> {}", id, currentStatus, newStatus);

        // Publikacja odpowiedniego zdarzenia
        if (newStatus == TaskStatus.DONE) {
            taskEventPublisher.publish(TaskEventType.TASK_COMPLETED, updatedTask, previousStatus);
        } else {
            taskEventPublisher.publish(TaskEventType.TASK_STATUS_CHANGED, updatedTask, previousStatus);
        }

        return TaskResponse.fromEntity(updatedTask);
    }

    public TaskResponse completeTask(Long id) {
        ChangeStatusRequest request = new ChangeStatusRequest(TaskStatus.DONE);
        return changeStatus(id, request);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksPaged(TaskStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Task> taskPage;

        if (status != null) {
            taskPage = taskRepository.findByStatus(status, pageable);
        } else {
            taskPage = taskRepository.findAll(pageable);
        }

        return taskPage.map(TaskResponse::fromEntity);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
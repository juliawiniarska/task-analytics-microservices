package com.taskapp.taskservice.service;

import com.taskapp.taskservice.dto.ChangeStatusRequest;
import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.exception.InvalidStatusTransitionException;
import com.taskapp.taskservice.exception.TaskNotFoundException;
import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskPriority;
import com.taskapp.taskservice.model.TaskStatus;
import com.taskapp.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Serwis realizujący logikę biznesową zarządzania zadaniami.
 * Odpowiada za operacje CRUD, zmianę statusu oraz oznaczanie jako ukończone.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Dozwolone przejścia między statusami zadania.
     * Wzorzec: State Machine (uproszczony).
     */
    private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED_TRANSITIONS = Map.of(
            TaskStatus.TODO, Set.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED),
            TaskStatus.IN_PROGRESS, Set.of(TaskStatus.TODO, TaskStatus.DONE, TaskStatus.CANCELLED),
            TaskStatus.DONE, Set.of(),        // Stan końcowy - brak przejść
            TaskStatus.CANCELLED, Set.of(TaskStatus.TODO)  // Można przywrócić anulowane
    );

    /**
     * Tworzy nowe zadanie na podstawie danych z żądania.
     */
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
        return TaskResponse.fromEntity(savedTask);
    }

    /**
     * Pobiera wszystkie zadania, opcjonalnie filtrowane po statusie.
     */
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

    /**
     * Pobiera pojedyncze zadanie po ID.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return TaskResponse.fromEntity(task);
    }

    /**
     * Aktualizuje dane zadania (tytuł, opis, priorytet, termin).
     * Aktualizowane są tylko pola, które zostały podane w żądaniu (nie-null).
     */
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
        return TaskResponse.fromEntity(updatedTask);
    }

    /**
     * Usuwa zadanie o podanym ID.
     */
    public void deleteTask(Long id) {
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
        log.info("Usunięto zadanie: id={}", id);
    }

    /**
     * Zmienia status zadania z walidacją dozwolonych przejść.
     * Implementuje uproszczoną maszynę stanów.
     */
    public TaskResponse changeStatus(Long id, ChangeStatusRequest request) {
        Task task = findTaskOrThrow(id);
        TaskStatus currentStatus = task.getStatus();
        TaskStatus newStatus = request.getStatus();

        // Walidacja przejścia między statusami
        Set<TaskStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        task.setStatus(newStatus);

        // Jeśli zadanie ukończone - zapisz datę ukończenia
        if (newStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Zmieniono status zadania: id={}, {} -> {}", id, currentStatus, newStatus);
        return TaskResponse.fromEntity(updatedTask);
    }

    /**
     * Oznacza zadanie jako ukończone (skrót dla zmiany statusu na DONE).
     * Dozwolone tylko z poziomu IN_PROGRESS.
     */
    public TaskResponse completeTask(Long id) {
        ChangeStatusRequest request = new ChangeStatusRequest(TaskStatus.DONE);
        return changeStatus(id, request);
    }

    /**
     * Pomocnicza metoda do wyszukiwania zadania lub rzucenia wyjątku.
     */
    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}

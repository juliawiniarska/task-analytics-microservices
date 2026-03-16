package com.taskapp.taskservice;

import com.taskapp.taskservice.dto.ChangeStatusRequest;
import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.exception.InvalidStatusTransitionException;
import com.taskapp.taskservice.exception.TaskNotFoundException;
import com.taskapp.taskservice.model.TaskPriority;
import com.taskapp.taskservice.model.TaskStatus;
import com.taskapp.taskservice.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy integracyjne dla TaskService.
 * Weryfikują poprawność logiki biznesowej usługi operacyjnej.
 */
@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    private CreateTaskRequest sampleRequest() {
        return CreateTaskRequest.builder()
                .title("Testowe zadanie")
                .description("Opis testowego zadania")
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Tworzenie nowego zadania")
    void shouldCreateTask() {
        TaskResponse response = taskService.createTask(sampleRequest());

        assertNotNull(response.getId());
        assertEquals("Testowe zadanie", response.getTitle());
        assertEquals("Opis testowego zadania", response.getDescription());
        assertEquals(TaskStatus.TODO, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertNotNull(response.getCreatedAt());
        assertNull(response.getCompletedAt());
    }

    @Test
    @DisplayName("Pobieranie zadania po ID")
    void shouldGetTaskById() {
        TaskResponse created = taskService.createTask(sampleRequest());
        TaskResponse fetched = taskService.getTaskById(created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getTitle(), fetched.getTitle());
    }

    @Test
    @DisplayName("Wyjątek przy próbie pobrania nieistniejącego zadania")
    void shouldThrowWhenTaskNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    @DisplayName("Pobieranie listy wszystkich zadań")
    void shouldGetAllTasks() {
        taskService.createTask(sampleRequest());
        taskService.createTask(CreateTaskRequest.builder().title("Drugie zadanie").build());

        List<TaskResponse> tasks = taskService.getAllTasks(null);
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Filtrowanie zadań po statusie")
    void shouldFilterByStatus() {
        taskService.createTask(sampleRequest());
        taskService.createTask(CreateTaskRequest.builder().title("Drugie").build());

        List<TaskResponse> todoTasks = taskService.getAllTasks(TaskStatus.TODO);
        assertEquals(2, todoTasks.size());

        List<TaskResponse> doneTasks = taskService.getAllTasks(TaskStatus.DONE);
        assertEquals(0, doneTasks.size());
    }

    @Test
    @DisplayName("Edycja danych zadania")
    void shouldUpdateTask() {
        TaskResponse created = taskService.createTask(sampleRequest());

        UpdateTaskRequest update = UpdateTaskRequest.builder()
                .title("Zmieniony tytuł")
                .priority(TaskPriority.LOW)
                .build();

        TaskResponse updated = taskService.updateTask(created.getId(), update);

        assertEquals("Zmieniony tytuł", updated.getTitle());
        assertEquals(TaskPriority.LOW, updated.getPriority());
        // Opis powinien pozostać bez zmian (nie był w żądaniu)
        assertEquals("Opis testowego zadania", updated.getDescription());
    }

    @Test
    @DisplayName("Usuwanie zadania")
    void shouldDeleteTask() {
        TaskResponse created = taskService.createTask(sampleRequest());
        taskService.deleteTask(created.getId());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(created.getId()));
    }

    @Test
    @DisplayName("Zmiana statusu: TODO -> IN_PROGRESS")
    void shouldChangeStatusToInProgress() {
        TaskResponse created = taskService.createTask(sampleRequest());

        TaskResponse updated = taskService.changeStatus(
                created.getId(),
                new ChangeStatusRequest(TaskStatus.IN_PROGRESS));

        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @DisplayName("Zmiana statusu: IN_PROGRESS -> DONE (ukończenie)")
    void shouldCompleteTask() {
        TaskResponse created = taskService.createTask(sampleRequest());

        // Najpierw TODO -> IN_PROGRESS
        taskService.changeStatus(created.getId(), new ChangeStatusRequest(TaskStatus.IN_PROGRESS));

        // Potem IN_PROGRESS -> DONE
        TaskResponse completed = taskService.completeTask(created.getId());

        assertEquals(TaskStatus.DONE, completed.getStatus());
        assertNotNull(completed.getCompletedAt());
    }

    @Test
    @DisplayName("Niedozwolona zmiana statusu: TODO -> DONE")
    void shouldRejectInvalidTransition() {
        TaskResponse created = taskService.createTask(sampleRequest());

        assertThrows(InvalidStatusTransitionException.class,
                () -> taskService.changeStatus(
                        created.getId(),
                        new ChangeStatusRequest(TaskStatus.DONE)));
    }

    @Test
    @DisplayName("Niedozwolona zmiana statusu: DONE -> IN_PROGRESS")
    void shouldRejectTransitionFromDone() {
        TaskResponse created = taskService.createTask(sampleRequest());
        taskService.changeStatus(created.getId(), new ChangeStatusRequest(TaskStatus.IN_PROGRESS));
        taskService.changeStatus(created.getId(), new ChangeStatusRequest(TaskStatus.DONE));

        assertThrows(InvalidStatusTransitionException.class,
                () -> taskService.changeStatus(
                        created.getId(),
                        new ChangeStatusRequest(TaskStatus.IN_PROGRESS)));
    }

    @Test
    @DisplayName("Domyślny priorytet MEDIUM gdy nie podano")
    void shouldUseDefaultPriority() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("Bez priorytetu")
                .build();

        TaskResponse response = taskService.createTask(request);
        assertEquals(TaskPriority.MEDIUM, response.getPriority());
    }
}

package com.taskapp.taskservice.controller;

import com.taskapp.taskservice.dto.ChangeStatusRequest;
import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.model.TaskStatus;
import com.taskapp.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Kontroler REST API dla operacji na zadaniach.
 * Udostępnia endpointy CRUD oraz zmianę statusu.
 *
 * Endpointy:
 *   POST   /api/tasks              - Tworzenie nowego zadania
 *   GET    /api/tasks              - Pobieranie listy zadań (opcjonalny filtr ?status=)
 *   GET    /api/tasks/{id}         - Pobieranie zadania po ID
 *   PUT    /api/tasks/{id}         - Edycja danych zadania
 *   DELETE /api/tasks/{id}         - Usuwanie zadania
 *   PATCH  /api/tasks/{id}/status  - Zmiana statusu zadania
 *   PATCH  /api/tasks/{id}/complete - Oznaczenie jako ukończone
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** Tworzenie nowego zadania */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Pobieranie listy zadań z opcjonalnym filtrem statusu */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        List<TaskResponse> tasks = taskService.getAllTasks(status);
        return ResponseEntity.ok(tasks);
    }

    /** Pobieranie pojedynczego zadania po ID */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    /** Edycja danych zadania */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    /** Usuwanie zadania */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /** Zmiana statusu zadania */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        TaskResponse response = taskService.changeStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /** Oznaczenie zadania jako ukończone */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        TaskResponse response = taskService.completeTask(id);
        return ResponseEntity.ok(response);
    }

    /** Pobieranie zadań z paginacją */
    @GetMapping("/paged")
    public ResponseEntity<Page<TaskResponse>> getTasksPaged(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskResponse> tasks = taskService.getTasksPaged(status, page, size);
        return ResponseEntity.ok(tasks);
    }
}

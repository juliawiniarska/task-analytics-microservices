package com.taskapp.taskservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Encja reprezentująca zadanie w systemie.
 * Stanowi główny model domenowy usługi operacyjnej.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tytuł zadania */
    @Column(nullable = false, length = 255)
    private String title;

    /** Szczegółowy opis zadania */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Aktualny status zadania */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    /** Priorytet zadania */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    /** Planowany termin realizacji */
    private LocalDate dueDate;

    /** Data i czas utworzenia zadania */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data i czas ostatniej modyfikacji */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Data i czas ukończenia zadania (null jeśli nieukończone) */
    private LocalDateTime completedAt;

    /**
     * Automatyczne ustawienie dat przy tworzeniu encji.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatyczna aktualizacja daty modyfikacji.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

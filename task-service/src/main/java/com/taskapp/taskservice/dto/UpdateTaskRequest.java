package com.taskapp.taskservice.dto;

import com.taskapp.taskservice.model.TaskPriority;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO dla żądania aktualizacji istniejącego zadania.
 * Wszystkie pola opcjonalne - aktualizowane są tylko podane wartości.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {

    @Size(max = 255, message = "Tytuł nie może przekraczać 255 znaków")
    private String title;

    @Size(max = 2000, message = "Opis nie może przekraczać 2000 znaków")
    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;
}

package com.taskapp.taskservice.dto;

import com.taskapp.taskservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dla żądania zmiany statusu zadania.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull(message = "Nowy status jest wymagany")
    private TaskStatus status;
}

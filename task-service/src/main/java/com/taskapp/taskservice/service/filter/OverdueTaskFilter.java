package com.taskapp.taskservice.service.filter;

import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Filtr zwracający zadania przeterminowane (po terminie, nieukończone).
 * Wzorzec projektowy: Strategy (konkretna strategia).
 */
@Component
public class OverdueTaskFilter implements TaskFilter {

    @Override
    public List<Task> apply(List<Task> tasks) {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getDueDate().isBefore(today))
                .filter(task -> task.getStatus() != TaskStatus.DONE
                        && task.getStatus() != TaskStatus.CANCELLED)
                .toList();
    }

    @Override
    public String getFilterName() {
        return "overdue";
    }
}
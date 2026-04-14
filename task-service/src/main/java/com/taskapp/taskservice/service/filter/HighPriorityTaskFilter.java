package com.taskapp.taskservice.service.filter;

import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskPriority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Filtr zwracający zadania o wysokim priorytecie.
 * Wzorzec projektowy: Strategy (konkretna strategia).
 */
@Component
public class HighPriorityTaskFilter implements TaskFilter {

    @Override
    public List<Task> apply(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getPriority() == TaskPriority.HIGH)
                .toList();
    }

    @Override
    public String getFilterName() {
        return "high-priority";
    }
}
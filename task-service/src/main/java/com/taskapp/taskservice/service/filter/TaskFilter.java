package com.taskapp.taskservice.service.filter;

import com.taskapp.taskservice.model.Task;
import java.util.List;

/**
 * Interfejs strategii filtrowania zadań.
 * Wzorzec projektowy: Strategy.
 */
public interface TaskFilter {
    List<Task> apply(List<Task> tasks);
    String getFilterName();
}
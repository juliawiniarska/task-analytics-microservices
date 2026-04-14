package com.taskapp.taskservice.service.filter;

import com.taskapp.taskservice.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serwis zarządzający strategiami filtrowania.
 * Automatycznie rejestruje wszystkie implementacje TaskFilter.
 * Wzorzec: Strategy + Service Locator.
 */
@Service
@RequiredArgsConstructor
public class TaskFilterService {

    private final List<TaskFilter> filters;

    /**
     * Zwraca mapę dostępnych filtrów po nazwie.
     */
    public Map<String, TaskFilter> getAvailableFilters() {
        return filters.stream()
                .collect(Collectors.toMap(TaskFilter::getFilterName, Function.identity()));
    }

    /**
     * Aplikuje filtr o podanej nazwie na liście zadań.
     */
    public List<Task> applyFilter(String filterName, List<Task> tasks) {
        TaskFilter filter = getAvailableFilters().get(filterName);
        if (filter == null) {
            throw new IllegalArgumentException("Nieznany filtr: " + filterName);
        }
        return filter.apply(tasks);
    }
}
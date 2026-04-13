package com.taskapp.taskservice.repository;

import com.taskapp.taskservice.model.Task;
import com.taskapp.taskservice.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium dostępu do danych zadań.
 * Rozszerza JpaRepository, dostarczając standardowe operacje CRUD
 * oraz niestandardowe zapytania z obsługą paginacji.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** Pobiera zadania o podanym statusie */
    List<Task> findByStatus(TaskStatus status);

    /** Pobiera zadania posortowane malejąco po dacie utworzenia */
    List<Task> findAllByOrderByCreatedAtDesc();

    /** Pobiera zadania o podanym statusie, posortowane po dacie utworzenia */
    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    /** Pobiera zadania z paginacją */
    Page<Task> findAll(Pageable pageable);

    /** Pobiera zadania filtrowane po statusie z paginacją */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}
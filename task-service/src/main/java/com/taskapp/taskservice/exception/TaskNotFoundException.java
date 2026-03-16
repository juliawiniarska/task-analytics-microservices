package com.taskapp.taskservice.exception;

/**
 * Wyjątek rzucany gdy zadanie o podanym ID nie zostanie znalezione.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Nie znaleziono zadania o ID: " + id);
    }
}

package com.taskapp.taskservice.model;

/**
 * Enum reprezentujący możliwe statusy zadania.
 * Odzwierciedla cykl życia zadania w systemie.
 */
public enum TaskStatus {
    TODO,           // Zadanie utworzone, oczekuje na rozpoczęcie
    IN_PROGRESS,    // Zadanie w trakcie realizacji
    DONE,           // Zadanie ukończone
    CANCELLED       // Zadanie anulowane
}

package com.taskapp.taskservice.exception;

import com.taskapp.taskservice.model.TaskStatus;

/**
 * Wyjątek rzucany przy próbie niedozwolonej zmiany statusu zadania.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TaskStatus from, TaskStatus to) {
        super("Niedozwolona zmiana statusu z " + from + " na " + to);
    }
}

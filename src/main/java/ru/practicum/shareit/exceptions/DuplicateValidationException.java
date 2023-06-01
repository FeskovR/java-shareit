package ru.practicum.shareit.exceptions;

public class DuplicateValidationException extends RuntimeException {
    public DuplicateValidationException(String message) {
        super(message);
    }
}

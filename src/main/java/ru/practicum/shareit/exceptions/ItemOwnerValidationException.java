package ru.practicum.shareit.exceptions;

public class ItemOwnerValidationException extends RuntimeException {
    public ItemOwnerValidationException(String message) {
        super(message);
    }
}

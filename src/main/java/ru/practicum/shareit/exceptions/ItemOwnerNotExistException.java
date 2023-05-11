package ru.practicum.shareit.exceptions;

public class ItemOwnerNotExistException extends RuntimeException {
    public ItemOwnerNotExistException() {

    }

    public ItemOwnerNotExistException(String message) {
        super(message);
    }
}

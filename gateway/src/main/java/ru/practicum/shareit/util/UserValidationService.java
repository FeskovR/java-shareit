package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public class UserValidationService {
    private static final String EMPTY_EMAIL_ERROR_MESSAGE = "email cannot be empty";
    private static final String WRONG_FORMAT_ERROR_MESSAGE = "wrong email format";
    private static final String EMPTY_NAME_ERROR_MESSAGE = "name cannot be empty";

    public static void validate(UserDto user) {
        if (user.getEmail() == null) {
            throw new ValidationException(EMPTY_EMAIL_ERROR_MESSAGE);
        }

        if (user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            throw new ValidationException(EMPTY_EMAIL_ERROR_MESSAGE);
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException(WRONG_FORMAT_ERROR_MESSAGE);
        }

        if (user.getName().isEmpty() ||
            user.getName().isBlank() ||
            user.getName() == null) {
            throw new ValidationException(EMPTY_NAME_ERROR_MESSAGE);
        }
    }
}

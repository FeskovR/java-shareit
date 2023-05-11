package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
public class UserValidationService {
    private static UserStorage userStorage;

    public UserValidationService(UserStorage userStorage) {
        UserValidationService.userStorage = userStorage;
    }

    private static final String EMPTY_EMAIL_ERROR_MESSAGE = "email cannot be empty";
    private static final String WRONG_FORMAT_ERROR_MESSAGE = "wrong email format";
    private static final String EMPTY_NAME_ERROR_MESSAGE = "name cannot be empty";
    private static final String DUPLICATE_EMAIL_ERROR_MESSAGE = "email has already been registered";

    public static void validate(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException(EMPTY_EMAIL_ERROR_MESSAGE);
        }

        if (
                user.getEmail().isBlank() ||
                user.getEmail().isEmpty()) {
            log.warn(EMPTY_EMAIL_ERROR_MESSAGE);
            throw new ValidationException(EMPTY_EMAIL_ERROR_MESSAGE);
        }

        if (!user.getEmail().contains("@")) {
            log.warn(WRONG_FORMAT_ERROR_MESSAGE);
            throw new ValidationException(WRONG_FORMAT_ERROR_MESSAGE);
        }

        if (
                user.getName().isEmpty() ||
                user.getName().isBlank() ||
                user.getName() == null) {
            log.warn(EMPTY_NAME_ERROR_MESSAGE);
            throw new ValidationException(EMPTY_NAME_ERROR_MESSAGE);
        }

        emailHasDuplicateValidation(user.getEmail());
    }

    public static void emailHasDuplicateValidation(String email) {
        for (User user : userStorage.findAllUsers()) {
            if (user.getEmail().equals(email))
                throw new DuplicateValidationException(DUPLICATE_EMAIL_ERROR_MESSAGE);
        }
    }
}

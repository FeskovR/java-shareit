package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;

    private final String EMPTY_EMAIL_ERROR_MESSAGE = "email cannot be empty";
    private final String WRONG_FORMAT_ERROR_MESSAGE = "wrong email format";
    private final String EMPTY_NAME_ERROR_MESSAGE = "name cannot be empty";
    private final String DUPLICATE_MAIL_ERROR_MESSAGE = "email has already been registered";
    private final String USER_NOT_FOUND_ERROR_MESSAGE = "user not found";

    public void validate(User user) {
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

    public void checkUserIsExist(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND_ERROR_MESSAGE);
        }
    }
}

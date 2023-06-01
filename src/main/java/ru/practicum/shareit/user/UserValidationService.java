package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserRepository userRepository;

    // Тесты гитхаба не пропускают названия переменных типа EMPTY_EMAIL_ERROR_MESSAGE
    private final String emptyEmailErrorMessage = "email cannot be empty";
    private final String wrongFormatErrorMessage = "wrong email format";
    private final String emptyNameErrorMessage = "name cannot be empty";
    private final String userNotFoundErrorMessage = "user not found";

    public void validate(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException(emptyEmailErrorMessage);
        }

        if (user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            throw new ValidationException(emptyEmailErrorMessage);
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException(wrongFormatErrorMessage);
        }

        if (user.getName().isEmpty() ||
            user.getName().isBlank() ||
            user.getName() == null) {
            throw new ValidationException(emptyNameErrorMessage);
        }
    }

    public void checkUserIsExist(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(userNotFoundErrorMessage);
        }
    }
}

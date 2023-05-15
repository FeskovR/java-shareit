package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private long id = 1;

    private static final String EMPTY_EMAIL_ERROR_MESSAGE = "email cannot be empty";
    private static final String WRONG_FORMAT_ERROR_MESSAGE = "wrong email format";
    private static final String EMPTY_NAME_ERROR_MESSAGE = "name cannot be empty";
    private static final String DUPLICATE_MAIL_ERROR_MESSAGE = "email has already been registered";

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validate(user);
        user.setId(id++);
        return userStorage.addUser(user);
    }

    public User updateUser(UserDto userDto, long userId) {
        User user = userStorage.findUserById(userId);

        if (userDto.getName() != null)
            user.setName(userDto.getName());

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            emailHasDuplicateValidation(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        return userStorage.updateUser(user);
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    //методы для валидации
    private void validate(User user) {
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

    private void emailHasDuplicateValidation(String email) {
//        for (User user : userStorage.findAllUsers()) {
//            if (user.getEmail().equals(email))
//                throw new DuplicateValidationException(DUPLICATE_MAIL_ERROR_MESSAGE);
//        }
        if (userStorage.getAllUserEmails().containsValue(email))
            throw new DuplicateValidationException(DUPLICATE_MAIL_ERROR_MESSAGE);
    }
}

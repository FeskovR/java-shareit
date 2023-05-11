package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private long id = 1;

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        UserValidationService.validate(user);
        user.setId(id++);
        return userStorage.addUser(user);
    }

    public User updateUser(UserDto userDto, long userId) {
        User user = userStorage.findUserById(userId);

        if (userDto.getName() != null)
            user.setName(userDto.getName());

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            UserValidationService.emailHasDuplicateValidation(userDto.getEmail());
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
}

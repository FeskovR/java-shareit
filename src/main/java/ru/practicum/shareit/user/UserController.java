package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * Получение всех пользователей списком
     */
    @GetMapping
    public List<User> findAllUsers() {
        log.info("Getting all users");

        return userService.findAllUsers();
    }

    /**
     * Добавление нового пользователя
     */
    @PostMapping
    public User addUser(@RequestBody UserDto userDto) {
        log.info("Adding new user");

        return userService.addUser(userDto);
    }

    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Updating used id: " + userId);

        return userService.updateUser(userDto, userId);
    }

    /**
     * Получение пользователя по ID
     */
    @GetMapping("/{userId}")
    public User findUserById(@PathVariable long userId) {
        log.info("Getting user by id: " + userId);

        return userService.findUserById(userId);
    }

    /**
     * Удаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user by id: " + userId);

        userService.deleteUser(userId);
    }
}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * Получение всех пользователей списком
     * @return list of all users
     */
    @GetMapping
    public List<User> findAllUsers() {
        log.info("Getting all users");
        return userService.findAllUsers();
    }

    /**
     * Добавление нового пользователя
     * @param userDto DTO for user
     * @return user
     */
    @PostMapping
    public User addUser(@RequestBody UserDto userDto) {
        log.info("Adding new user");
        return userService.addUser(userDto);
    }

    /**
     * Обновление пользователя
     * @param userDto DTO for user
     * @param userId long user ID
     * @return user
     */
    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Updating user id: " + userId);
        return userService.updateUser(userDto, userId);
    }

    /**
     * Получение пользователя по ID
     * @param userId long user ID
     * @return user
     */
    @GetMapping("/{userId}")
    public User findUserById(@PathVariable long userId) {
        log.info("Getting user by id: " + userId);
        return userService.findUserById(userId);
    }

    /**
     * Удаление пользователя
     * @param userId long user ID
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user by id: " + userId);
        userService.deleteUser(userId);
    }
}

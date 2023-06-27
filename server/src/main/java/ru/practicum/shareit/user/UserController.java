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

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Getting all users");
        return userService.findAllUsers();
    }

    @PostMapping
    public User addUser(@RequestBody UserDto userDto) {
        log.info("Adding new user");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Updating user id: {}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable long userId) {
        log.info("Getting user by id: {}", userId);
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user by id: {}", userId);
        userService.deleteUser(userId);
    }
}

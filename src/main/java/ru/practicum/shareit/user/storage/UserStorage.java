package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> findAllUsers();

    User addUser(User user);

    User findUserById(long id);

    User updateUser(User user);

    void deleteUser(long userId);

    Map<Long, String> getAllUserEmails();
}

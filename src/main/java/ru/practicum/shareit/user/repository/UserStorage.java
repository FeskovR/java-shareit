package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User addUser(User user);

    User findUserById(long id);

    User updateUser(User user);

    void deleteUser(long userId);
}

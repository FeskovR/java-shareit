package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private long id = 1;

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        emails.put(user.getId(), user.getEmail());
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        emails.put(user.getId(), user.getEmail());
        return user;
    }

    public User findUserById(long id) {
        return users.get(id);
    }

    public void deleteUser(long id) {
        users.remove(id);
        emails.remove(id);
    }

    public Map<Long, String> getAllUserEmails() {
        return emails;
    }
}

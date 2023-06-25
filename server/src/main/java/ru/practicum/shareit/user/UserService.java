package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userRepository.save(user);
    }

    public User updateUser(UserDto userDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        User updatingUser = UserMapper.toUser(userDto);

        if (updatingUser.getName() == null) {
            updatingUser.setName(user.getName());
        }
        if (updatingUser.getEmail() == null) {
            updatingUser.setEmail(user.getEmail());
        }
        updatingUser.setId(user.getId());
        return userRepository.save(updatingUser);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

}

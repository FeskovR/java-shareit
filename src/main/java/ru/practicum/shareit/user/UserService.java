package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userValidationService.validate(user);
        return userRepository.save(user);
    }

    public User updateUser(UserDto userDto, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User updatingUser = UserMapper.toUser(userDto);

            if (updatingUser.getName() == null) {
                updatingUser.setName(user.get().getName());
            }
            if (updatingUser.getEmail() == null) {
                updatingUser.setEmail(user.get().getEmail());
            }
            userValidationService.validate(updatingUser);
            updatingUser.setId(user.get().getId());
            return userRepository.save(updatingUser);
        } else {
            throw new NotFoundException("User for updating not found");
        }
    }

    public User findUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

}

package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User(
                0,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}

package ru.practicum.shareit.user;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User(
                0,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}

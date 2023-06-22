package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUser() {
        UserDto userDto = new UserDto("Username", "mail@zz.zz");
        User user = UserMapper.toUser(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    private final UserService userService;

    UserDto user1Dto = new UserDto("User1", "mail1@zz.zz");
    UserDto user2Dto = new UserDto("User2", "mail2zz.zz");
    UserDto user3Dto = new UserDto("User3", "mail3@zz.zz");

    @Test
    void addUserTest() {
        User returnedUser = userService.addUser(user1Dto);

        assertEquals(user1Dto.getName(), returnedUser.getName());
        assertEquals(user1Dto.getEmail(), returnedUser.getEmail());
        assertEquals(1, returnedUser.getId());

        assertThrows(ValidationException.class, () -> userService.addUser(user2Dto));
    }

    @Test
    void updateUserTest() {
        userService.addUser(user1Dto);
        User returnedUser = userService.updateUser(user3Dto, 1L);

        assertEquals(user3Dto.getName(), returnedUser.getName());
        assertEquals(user3Dto.getEmail(), returnedUser.getEmail());
        assertEquals(1, returnedUser.getId());

        assertThrows(ValidationException.class, () -> userService.updateUser(user2Dto, 1L));
        assertThrows(NotFoundException.class, () -> userService.updateUser(user3Dto, 2L));
    }

    @Test
    void findUserByIdTest() {
        userService.addUser(user1Dto);
        User returnedUser = userService.findUserById(1L);

        assertEquals(user1Dto.getName(), returnedUser.getName());
        assertEquals(user1Dto.getEmail(), returnedUser.getEmail());
        assertEquals(1L, returnedUser.getId());

        assertThrows(NotFoundException.class, () -> userService.findUserById(2L));
    }

    @Test
    void deleteUser() {
        User returnedUser = userService.addUser(user1Dto);

        assertEquals(user1Dto.getName(), returnedUser.getName());
        assertEquals(user1Dto.getEmail(), returnedUser.getEmail());
        assertEquals(1, returnedUser.getId());

        userService.deleteUser(1L);

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void findAllUsersTest() {
        List<User> returnedUserList = userService.findAllUsers();

        assertEquals(0, returnedUserList.size());

        userService.addUser(user1Dto);
        userService.addUser(user3Dto);

        List<User> newReturnedUserList = userService.findAllUsers();

        assertEquals(2, newReturnedUserList.size());
    }
}

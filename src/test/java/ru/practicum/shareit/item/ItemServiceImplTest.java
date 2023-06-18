package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;

    ItemDto itemDto1 = new ItemDto(0, "item1", "desc1", true, 0);
    ItemDto itemDto2 = new ItemDto(0, "item2", "desc2", false, 1);
    ItemDto itemDto3 = new ItemDto(0, null, "desc2", false, 1);
    UserDto userDto = new UserDto("user1", "mail@zz.zz");
    UserDto userDto2 = new UserDto("user2", "mail2@zz.zz");
    CommentDto commentDto = new CommentDto(0, "Comment", "Author", LocalDateTime.now());

    @Test
    void addItemTest() {
        userService.addUser(userDto);
        ItemDto returnedItem = itemService.addItem(itemDto1, 1L);

        assertEquals(itemDto1.getName(), returnedItem.getName());
        assertEquals(itemDto1.getDescription(), returnedItem.getDescription());
        assertEquals(itemDto1.getRequestId(), returnedItem.getRequestId());

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto1, 2L));
        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto3, 1L));
    }

    @Test
    void updateItemTest() {
        userService.addUser(userDto);
        itemService.addItem(itemDto1, 1L);

        ItemDto returnedItemDto = itemService.updateItem(itemDto2, 1L, 1L);

        assertEquals(itemDto2.getName(), returnedItemDto.getName());
        assertEquals(itemDto2.getDescription(), returnedItemDto.getDescription());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto2, 2L, 1L));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto2, 1L, 2L));
    }

    @Test
    void findItemByIdTest() {
        userService.addUser(userDto);
        itemService.addItem(itemDto1, 1L);

        ItemDtoWithBookings returned = itemService.findItemById(1L, 1L);

        assertEquals(itemDto1.getName(), returned.getName());
        assertEquals(itemDto1.getDescription(), returned.getDescription());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(2L, 1L));
        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L, 2L));
    }

    @Test
    void findAllByOwnerIdTest() {
        userService.addUser(userDto);

        assertEquals(0, itemService.findAllByOwnerId(1L, 1, 20).size());

        itemService.addItem(itemDto1, 1L);
        itemService.addItem(itemDto2, 1L);

        assertEquals(2, itemService.findAllByOwnerId(1L, 1, 20).size());

        assertThrows(NotFoundException.class, () -> itemService.findAllByOwnerId(2L, 0, 20));
    }

    @Test
    void searchByTextTest() {
        userService.addUser(userDto);
        itemService.addItem(itemDto1, 1L);

        List<ItemDto> returned = itemService.searchByText("desc1", 0, 20);

        assertEquals(itemDto1.getName(), returned.get(0).getName());
    }

    @Test
    void addCommentTest() {
        userService.addUser(userDto);
        userService.addUser(userDto2);
        itemService.addItem(itemDto1, 1L);

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 2L, commentDto));
    }
}

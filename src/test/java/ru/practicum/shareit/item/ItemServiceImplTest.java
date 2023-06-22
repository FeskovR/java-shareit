package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
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
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    ItemDto itemDto1 = new ItemDto(0, "item1", "desc1", true, 0);
    ItemDto itemDto2 = new ItemDto(0, null, null, null, 1);
    ItemDto itemDto3 = new ItemDto(0, "item2", "desc2", false, 1);
    UserDto userDto = new UserDto("user1", "mail@zz.zz");
    UserDto userDto2 = new UserDto("user2", "mail2@zz.zz");
    CommentDto commentDto = new CommentDto(0, "Comment", "Author", LocalDateTime.now());
    CommentDto invalidCommentDto = new CommentDto(0, "", "Author", LocalDateTime.now());

    @Test
    void addItemTest() {
        userService.addUser(userDto);
        ItemDto returnedItem = itemService.addItem(itemDto1, 1L);

        assertEquals(itemDto1.getName(), returnedItem.getName());
        assertEquals(itemDto1.getDescription(), returnedItem.getDescription());
        assertEquals(itemDto1.getRequestId(), returnedItem.getRequestId());

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto1, 2L));
        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto2, 1L));
    }

    @Test
    void updateItemTest() {
        userService.addUser(userDto);
        itemService.addItem(itemDto1, 1L);
        userService.addUser(userDto2);

        ItemDto returnedItemDto = itemService.updateItem(itemDto2, 1L, 1L);

        assertEquals("item1", returnedItemDto.getName());
        assertEquals("desc1", returnedItemDto.getDescription());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto2, 3L, 1L));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto2, 1L, 2L));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto2, 2L, 1L));
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
        userService.addUser(userDto2);

        assertEquals(0, itemService.findAllByOwnerId(1L, 1, 20).size());

        itemService.addItem(itemDto1, 1L);
        itemService.addItem(itemDto3, 1L);

        assertEquals(2, itemService.findAllByOwnerId(1L, 1, 20).size());

        assertThrows(NotFoundException.class, () -> itemService.findAllByOwnerId(3L, 0, 20));
        assertThrows(ValidationException.class, () -> itemService.findAllByOwnerId(1L, -1, 0));

        User booker = UserMapper.toUser(userDto);
        booker.setId(2L);
        Item item = ItemMapper.toItem(itemDto2, booker);
        item.setId(1L);
        Booking pastBooking1 = new Booking(1L,
                LocalDateTime.now().minusHours(4),
                LocalDateTime.now().minusHours(3),
                item,
                booker,
                Status.WAITING);
        Booking nextBooking1 = new Booking(2L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING);
        Booking pastBooking2 = new Booking(3L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item,
                booker,
                Status.WAITING);
        Booking nextBooking2 = new Booking(4L,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                item,
                booker,
                Status.WAITING);
        bookingRepository.save(pastBooking1);
        bookingRepository.save(nextBooking1);
        bookingRepository.save(pastBooking2);
        bookingRepository.save(nextBooking2);

        assertEquals(2, itemService.findAllByOwnerId(1L, 1, 20).size());
        assertEquals(3L, itemService.findAllByOwnerId(1L, 0, 20).get(0).getLastBooking().getId());
    }

    @Test
    void searchByTextTest() {
        userService.addUser(userDto);
        itemService.addItem(itemDto1, 1L);

        List<ItemDto> returned = itemService.searchByText("desc1", 0, 20);

        assertEquals(itemDto1.getName(), returned.get(0).getName());
        assertEquals(0, itemService.searchByText("", 0, 20).size());

        assertThrows(ValidationException.class, () -> itemService.searchByText("", -1, 0));
    }

    @Test
    void addCommentTest() {
        userService.addUser(userDto);
        userService.addUser(userDto2);
        itemService.addItem(itemDto1, 1L);
        User user = UserMapper.toUser(userDto);
        user.setId(1L);
        Item item = ItemMapper.toItem(itemDto2, user);
        item.setId(1L);
        Booking booking = new Booking(1L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item,
                user,
                Status.WAITING);
        bookingRepository.save(booking);

        CommentDto returned = itemService.addComment(1L, 1L, commentDto);

        assertEquals(commentDto.getText(), returned.getText());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 2L, commentDto));
        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 2L, invalidCommentDto));
        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 2L, commentDto));
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 3L, commentDto));
    }
}

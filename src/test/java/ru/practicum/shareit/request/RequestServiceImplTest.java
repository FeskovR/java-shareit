package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
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
public class RequestServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService requestService;

    UserDto requestorDto = new UserDto("owner", "owner@zz.zz");
    UserDto userDto = new UserDto("booker", "booker@zz.zz");
    UserDto user1Dto = new UserDto("user", "user@zz.zz");
    ItemDto item1Dto = new ItemDto(0, "Item1", "Desc1", true, 0);
    ItemDto itemWithRequestIdDto = new ItemDto(0, "Item", "With request id", true, 1);
    ItemDto item2Dto = new ItemDto(0, "Item2", "Desc2", false, 0);
    BookingDto booking1Dto = new BookingDto(1L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12, 12, 12, 12));
    BookingDto booking2Dto = new BookingDto(2L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12, 12, 12, 12));
    ItemRequestDto requestDto = new ItemRequestDto("DESC");

    @Test
    void addItemRequestTest() {
        User requestor = userService.addUser(requestorDto);

        ItemRequest returned = requestService.addItemRequest(requestDto, requestor.getId());

        assertEquals(requestDto.getDescription(), returned.getDescription());
        assertEquals(1L, returned.getId());
        assertEquals(requestor.getId(), returned.getRequestor().getId());

        assertThrows(NotFoundException.class, () -> requestService.addItemRequest(requestDto, 2L));
    }

    @Test
    void findItemRequestByIdTest() {
        User requestor = userService.addUser(requestorDto);
        ItemRequest request = requestService.addItemRequest(requestDto, requestor.getId());

        ItemRequestDtoWithItems returned = requestService.findItemRequestById(1L, requestor.getId());

        assertEquals(requestDto.getDescription(), returned.getDescription());
        assertThrows(NotFoundException.class, () -> requestService.findItemRequestById(2L, requestor.getId()));
        assertThrows(NotFoundException.class, () -> requestService.findItemRequestById(1L, 3L));
    }

    @Test
    void findAllUserRequestsTest() {
        User requestor = userService.addUser(requestorDto);
        User owner = userService.addUser(userDto);
        ItemRequest request = requestService.addItemRequest(requestDto, requestor.getId());
        itemService.addItem(itemWithRequestIdDto, owner.getId());

        List<ItemRequestDtoWithItems> returned = requestService.findAllUserRequests(requestor.getId());

        assertEquals(1, returned.size());
        assertEquals(requestDto.getDescription(), returned.get(0).getDescription());
        assertThrows(NotFoundException.class, () -> requestService.findAllUserRequests(3L));
    }

    @Test
    void findAllRequestsTest() {
        User requestor = userService.addUser(requestorDto);
        User owner = userService.addUser(user1Dto);
        User user = userService.addUser(userDto);
        ItemRequest request = requestService.addItemRequest(requestDto, requestor.getId());
        itemService.addItem(itemWithRequestIdDto, owner.getId());

        List<ItemRequestDtoWithItems> returned = requestService.findAllRequests(user.getId(), 1, 20);

        assertEquals(1, returned.size());
        assertEquals(requestDto.getDescription(), returned.get(0).getDescription());
        assertThrows(NotFoundException.class, () -> requestService.findAllRequests(4L, 1, 20));
        assertThrows(ValidationException.class, () -> requestService.findAllRequests(user.getId(), -1, 0));
    }
}

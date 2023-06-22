package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
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
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    UserDto ownerDto = new UserDto("owner", "owner@zz.zz");
    UserDto bookerDto = new UserDto("booker", "booker@zz.zz");
    ItemDto item1Dto = new ItemDto(0, "Item1", "Desc1", true, 0);
    ItemDto item2Dto = new ItemDto(0, "Item2", "Desc2", false, 0);
    BookingDto booking1Dto = new BookingDto(1L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12, 12, 12, 12));
    BookingDto booking2Dto = new BookingDto(2L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12, 12, 12, 12));
    BookingDto booking3Dto = new BookingDto(3L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12, 12, 12, 12));

    @Test
    void addBookingTest() {
        User owner = userService.addUser(ownerDto);
        User booker = userService.addUser(bookerDto);
        itemService.addItem(item1Dto, 1L);
        itemService.addItem(item2Dto, 1L);

        Booking returned = bookingService.addBooking(booking1Dto, booker.getId());

        assertEquals(1L, returned.getItem().getId());
        assertEquals(booking1Dto.getStart(), returned.getStart());
        assertEquals(booking1Dto.getEnd(), returned.getEnd());
        assertEquals(Status.WAITING, returned.getStatus());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(booking1Dto, 3L));
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(booking3Dto, 2L));
        assertThrows(ValidationException.class, () -> bookingService.addBooking(booking2Dto, 2L));
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(booking1Dto, 1L));
    }

    @Test
    void setOwnersDecisionTest() {
        User owner = userService.addUser(ownerDto);
        User booker = userService.addUser(bookerDto);
        itemService.addItem(item1Dto, 1L);
        itemService.addItem(item1Dto, 1L);
        bookingService.addBooking(booking1Dto, booker.getId());
        bookingService.addBooking(booking2Dto, booker.getId());

        Booking returned = bookingService.setOwnersDecision(1L, true, owner.getId());
        Booking returnedRejected = bookingService.setOwnersDecision(2L, false, owner.getId());

        assertEquals(Status.APPROVED, returned.getStatus());
        assertEquals(Status.REJECTED, returnedRejected.getStatus());

        assertThrows(NotFoundException.class, () -> bookingService.setOwnersDecision(3L, true, owner.getId()));
        assertThrows(NotFoundException.class, () -> bookingService.setOwnersDecision(1L, true, 3L));
        assertThrows(ValidationException.class, () -> bookingService.setOwnersDecision(1L, true, owner.getId()));
        assertThrows(NotFoundException.class, () -> bookingService.setOwnersDecision(2L, false, booker.getId()));
    }

    @Test
    void findBookingByIdTest() {
        User owner = userService.addUser(ownerDto);
        User booker = userService.addUser(bookerDto);
        ItemDto item = itemService.addItem(item1Dto, owner.getId());
        Booking booking = bookingService.addBooking(booking1Dto, booker.getId());

        Booking returned = bookingService.findBookingById(1L, owner.getId());

        assertEquals(booking1Dto.getStart(), returned.getStart());
        assertEquals(booking1Dto.getEnd(), returned.getEnd());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(3L, owner.getId()));
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(1L, 3L));
    }

    @Test
    void findAllByBookerTest() {
        User owner = userService.addUser(ownerDto);
        User booker = userService.addUser(bookerDto);
        ItemDto item1 = itemService.addItem(item1Dto, owner.getId());
        ItemDto item2 = itemService.addItem(item2Dto, owner.getId());
        Booking booking1 = bookingService.addBooking(booking1Dto, booker.getId());

        List<Booking> returnedList = bookingService.findAllByBooker(booker.getId(), "ALL", 1, 20);
        List<Booking> returnedList2 = bookingService.findAllByBooker(booker.getId(), "CURRENT", 1, 20);
        List<Booking> returnedList3 = bookingService.findAllByBooker(booker.getId(), "PAST", 1, 20);
        List<Booking> returnedList4 = bookingService.findAllByBooker(booker.getId(), "FUTURE", 1, 20);
        List<Booking> returnedList5 = bookingService.findAllByBooker(booker.getId(), "WAITING", 1, 20);
        List<Booking> returnedList6 = bookingService.findAllByBooker(booker.getId(), "REJECTED", 1, 20);

        assertEquals(1, returnedList.size());
        assertThrows(NotFoundException.class, () -> bookingService.findAllByBooker(3L, "ALL", 1, 20));
        assertThrows(UnknownStateException.class, () -> bookingService.findAllByBooker(booker.getId(), "WRONG STATE", 1, 20));
        assertThrows(ValidationException.class, () -> bookingService.findAllByBooker(booker.getId(), "CURRENT", -1, 0));
    }

    @Test
    void findAllByOwnerTest() {
        User owner = userService.addUser(ownerDto);
        User booker = userService.addUser(bookerDto);
        ItemDto item1 = itemService.addItem(item1Dto, owner.getId());
        ItemDto item2 = itemService.addItem(item2Dto, owner.getId());
        Booking booking1 = bookingService.addBooking(booking1Dto, booker.getId());

        List<Booking> returnedList = bookingService.findAllByOwner(owner.getId(), "ALL", 1, 20);
        List<Booking> returnedList2 = bookingService.findAllByOwner(owner.getId(), "CURRENT", 1, 20);
        List<Booking> returnedList3 = bookingService.findAllByOwner(owner.getId(), "PAST", 1, 20);
        List<Booking> returnedList4 = bookingService.findAllByOwner(owner.getId(), "FUTURE", 1, 20);
        List<Booking> returnedList5 = bookingService.findAllByOwner(owner.getId(), "WAITING", 1, 20);
        List<Booking> returnedList6 = bookingService.findAllByOwner(owner.getId(), "REJECTED", 1, 20);


        assertEquals(1, returnedList.size());
        assertThrows(NotFoundException.class, () -> bookingService.findAllByOwner(3L, "ALL", 1, 20));
        assertThrows(UnknownStateException.class, () -> bookingService.findAllByOwner(booker.getId(), "WRONG STATE", 1, 20));
        assertThrows(ValidationException.class, () -> bookingService.findAllByOwner(owner.getId(), "ALL", -1, 0));
    }
}

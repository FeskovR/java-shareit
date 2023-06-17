package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    User user1 = new User(1, "User1", "email@email.com");
    User user2 = new User(2, "User2", "email@email.com");
    Item item1 = new Item(1, "Название1", "Описание1", true, user1, 0);
    Item item2 = new Item(2, "Название2", "Описание2", true, user2, 0);
    BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.of(2025, 5, 1, 12, 0),
            LocalDateTime.of(2025, 6, 1, 12, 0));
    Booking booking = new Booking(1,
            LocalDateTime.of(2025, 5, 1, 12, 0),
            LocalDateTime.of(2025, 6, 1, 12, 0),
            item2,
            user1,
            Status.WAITING);

    @Test
    void addBookingWithValidBookingDtoAndBookerThenSaveBooking() {
        Booking bookingToSave = BookingMapper.toBooking(bookingDto, item1, user2, Status.WAITING);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingToSave);

        Booking returnedBooking = bookingService.addBooking(bookingDto, 1L);

        assertEquals(bookingToSave, returnedBooking);
    }

    @Test
    void addBookingWithInvalidBookingDtoThenThrowValidationException() {
        BookingDto InvalidBookingDto = new BookingDto();

        assertThrows(ValidationException.class, () -> bookingService.addBooking(InvalidBookingDto, 1L));
    }

    @Test
    void addBookingWithEmptyItemOptionalThenThrowNotFoundException() {
        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, 1L));
    }

    @Test
    void addBookingWithEmptyUserOptionalThenThrowNotFoundException() {
        long bookerId = 1L;
        Mockito.when(itemRepository.findById(bookingDto.getItemId()))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(bookerId))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, bookerId));
    }

    @Test
    void setOwnersDecisionWithValidBookingThenSaveBooking() {
        Booking bookingToSave = booking;
        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingToSave));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingToSave);

        Booking returnedBooking = bookingService.setOwnersDecision(1L, true, 2L);

        assertEquals(bookingToSave, returnedBooking);
    }

    @Test
    void findBookingByIdWithValidBookingAndUser() {
        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        Booking returnedBooking = bookingService.findBookingById(1L, 2L);

        assertEquals(booking, returnedBooking);
    }

    @Test
    void findAllByBookerWhenValidUserThenReturnListOfBookings() {
        List<Booking> bookingList = new ArrayList<>();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllBookingsByBooker(user1, getPageable(1, 20)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        List<Booking> returnedBookings = bookingService.findAllByBooker(1L, "ALL", 1, 20);

        assertEquals(bookingList, returnedBookings);
    }

    @Test
    void findAllByOwnerWhenValidUserThenReturnListOfBookings() {
        List<Booking> bookingList = new ArrayList<>();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findAllBookingsByOwner(user1, getPageable(1, 20)))
                .thenReturn(bookingList);

        List<Booking> returnedBookings = bookingService.findAllByOwner(1L, "ALL", 1, 20);

        assertEquals(bookingList, returnedBookings);
    }

    private Pageable getPageable(long from, int size) {
        int page = (int) (from / size);
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        return PageRequest.of(page, size, sort);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemValidationService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidationService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingValidationService bookingValidationService;
    private final UserValidationService userValidationService;
    private final ItemValidationService itemValidationService;

    @Override
    public Booking addBooking(BookingDto bookingDto, long bookerId) {
        bookingValidationService.validate(bookingDto);
        userValidationService.checkUserIsExist(bookerId);
        itemValidationService.checkItemIsExist(bookingDto.getItemId());

        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (!item.getAvailable())
            throw new ValidationException("Item is not available");
        User booker = userRepository.findById(bookerId).get();

        if (booker.getId() == item.getOwner().getId())
            throw new NotFoundException("Owner cannot book his item");

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setOwnersDecision(long bookingId, boolean approved, long ownerId) {
        bookingValidationService.checkBookingIsExist(bookingId);
        userValidationService.checkUserIsExist(ownerId);

        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Booking is already approved");
        }
        User owner = userRepository.findById(ownerId).get();

        if (owner.getId() != booking.getItem().getOwner().getId())
            throw new NotFoundException("User is not owner by item");

        if (approved)
            booking.setStatus(Status.APPROVED);
        else
            booking.setStatus(Status.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(long bookingId, long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Booking not found");
        } else {
            Booking booking = bookingOpt.get();

            if (booking.getBooker().getId() == userId ||
            booking.getItem().getOwner().getId() == userId) {
                return booking;
            } else {
                throw new NotFoundException("User do not have access to this booking");
            }
        }
    }

    @Override
    public List<Booking> findAllByBooker(long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        userValidationService.checkUserIsExist(userId);
        User user = userRepository.findById(userId).get();

        switch (state) {
            case "ALL":
                return bookingRepository.findAllBookingsByBooker(user);
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByBooker(user, now);
            case "PAST":
                return bookingRepository.findPastBookingsByBooker(user, now);
            case "FUTURE":
                return bookingRepository.findFutureBookingsByBooker(user, now);
            case "WAITING":
                return bookingRepository.findWaitingBookingsByBooker(user);
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByBooker(user);
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> findAllByOwner(long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        userValidationService.checkUserIsExist(userId);
        User user = userRepository.findById(userId).get();

        switch (state) {
            case "ALL":
                List<Booking> books =  bookingRepository.findAllBookingsByOwner(user);
                return books;
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByOwner(user, now);
            case "PAST":
                return bookingRepository.findPastBookingsByOwner(user, now);
            case "FUTURE":
                return bookingRepository.findFutureBookingsByOwner(user, now);
            case "WAITING":
                return bookingRepository.findWaitingBookingsByOwner(user);
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByOwner(user);
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }
}

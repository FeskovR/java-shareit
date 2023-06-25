package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking addBooking(BookingDto bookingDto, long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item id: " + bookingDto.getItemId() + " not found")
        );
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new NotFoundException("User id: " + bookerId + " not found"));

        if (!item.getAvailable())
            throw new ValidationException("Item is not available");

        if (booker.getId() == item.getOwner().getId())
            throw new NotFoundException("Owner cannot book his item");

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setOwnersDecision(long bookingId, boolean approved, long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking id " + bookingId + " not found")
        );
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User id " + ownerId + " not found")
        );

        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Booking is already approved");
        }

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
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking id: " + bookingId + " not found")
        );

        if (booking.getBooker().getId() == userId ||
        booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new NotFoundException("User do not have access to this booking");
        }

    }

    @Override
    public List<Booking> findAllByBooker(long userId, String state, long from, int size) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (from < 0 || size < 1) {
            throw new ValidationException("Pageable validation error");
        }

        int page = (int) (from / size);
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        Pageable pageable = PageRequest.of(page, size, sort);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllBookingsByBooker(user, pageable).toList();
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByBooker(user, now);
            case "PAST":
                return bookingRepository.findPastBookingsByBooker(user, now, pageable).toList();
            case "FUTURE":
                return bookingRepository.findFutureBookingsByBooker(user, now, pageable).toList();
            case "WAITING":
                return bookingRepository.findWaitingBookingsByBooker(user, pageable).toList();
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByBooker(user, pageable).toList();
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> findAllByOwner(long userId, String state, long from, int size) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (from < 0 || size < 1) {
            throw new ValidationException("Pageable validation error");
        }

        int page = (int) (from / size);
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        Pageable pageable = PageRequest.of(page, size, sort);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllBookingsByOwner(user, pageable).toList();
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByOwner(user, now, pageable).toList();
            case "PAST":
                return bookingRepository.findPastBookingsByOwner(user, now, pageable).toList();
            case "FUTURE":
                return bookingRepository.findFutureBookingsByOwner(user, now, pageable).toList();
            case "WAITING":
                return bookingRepository.findWaitingBookingsByOwner(user, pageable).toList();
            case "REJECTED":
                return bookingRepository.findRejectedBookingsByOwner(user, pageable).toList();
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }
}

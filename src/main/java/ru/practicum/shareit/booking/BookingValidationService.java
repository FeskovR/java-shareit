package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final BookingRepository bookingRepository;

    public void validate(BookingDto bookingDto) {
        if (bookingDto.getItemId() == 0 ||
        bookingDto.getStart() == null ||
        bookingDto.getEnd() == null ||
        bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
        bookingDto.getStart().isBefore(LocalDateTime.now()) ||
        bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
        bookingDto.getStart().isEqual(bookingDto.getEnd())
        ) {
            throw new ValidationException("WRONG booking request");
        }
    }

    public void checkBookingIsExist(long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty())
            throw new NotFoundException("Booking not found");
    }
}

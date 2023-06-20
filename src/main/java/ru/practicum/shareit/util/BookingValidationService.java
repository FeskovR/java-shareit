package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

public class BookingValidationService {

    public static void validate(BookingDto bookingDto) {
        if (bookingDto.getItemId() == 0 ||
        bookingDto.getStart() == null ||
        bookingDto.getEnd() == null ||
        bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
        bookingDto.getStart().plusSeconds(15).isBefore(LocalDateTime.now()) ||
        bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
        bookingDto.getStart().isEqual(bookingDto.getEnd())
        ) {
            throw new ValidationException("WRONG booking request");
        }
    }
}

package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

public class BookingValidationServiceTest {
    BookingDto validBookingDto = new BookingDto(1L,
            LocalDateTime.of(2099, 12,12,12,12),
            LocalDateTime.of(2100, 12,12,12,12));
    BookingDto invalidBookingDto = new BookingDto(1L,
            LocalDateTime.of(1999, 12,12,12,12),
            LocalDateTime.of(2000, 12,12,12,12));

    @Test
    void validateValidBookingDtoTest() {
        assertDoesNotThrow(() -> BookingValidationService.validate(validBookingDto));
    }

    @Test
    void validateInvalidBookingDtoTest() {
        assertThrows(ValidationException.class, () -> BookingValidationService.validate(invalidBookingDto));
    }
}

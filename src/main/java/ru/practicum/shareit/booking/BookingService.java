package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto bookingDto, long bookerId);
    Booking setOwnersDecision(long bookingId, boolean approved, long ownerId);
    Booking findBookingById(long bookingId, long userId);
    List<Booking> findAllByBooker(long userId, String state);
    List<Booking> findAllByOwner(long userId, String state);
}

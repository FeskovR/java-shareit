package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User booker, Status status) {
        return new Booking(0,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                status);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public static BookingShort toBookingShort(Booking booking) {
        BookingShort bookingShort = new BookingShort();
        bookingShort.setId(booking.getId());
        bookingShort.setBookerId(booking.getBooker().getId());
        return bookingShort;
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final String ownerIdHeaderTitle = "x-sharer-user-id";

    /**
     * Добавление нового бронирования
     * @param bookingDto DTO for booking
     * @param booker booker ID
     * @return Booking
     */
    @PostMapping
    public Booking addBooking(@RequestBody BookingDto bookingDto,
                              @RequestHeader(ownerIdHeaderTitle) long booker) {
        log.info("Adding new booking for item id: {} by booker id: {}", bookingDto.getItemId(), booker);
        return bookingService.addBooking(bookingDto, booker);
    }

    /**
     * Ответ владельца на бронирование
     * @param bookingId booking ID
     * @param approved owners decision
     * @param ownerId owner ID
     * @return Booking
     */
    @PatchMapping("/{bookingId}")
    public Booking setOwnersDecision(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(ownerIdHeaderTitle) long ownerId) {
        log.info("Owner id: {} chooses decision", ownerId);
        return bookingService.setOwnersDecision(bookingId, approved, ownerId);
    }

    /**
     * Получение бронирования по ID
     * @param bookingId booking ID
     * @param userId user ID
     * @return Booking
     */
    @GetMapping("/{bookingId}")
    public Booking findBookingById(@PathVariable long bookingId,
                                   @RequestHeader(ownerIdHeaderTitle) long userId) {
        log.info("Getting booking id: {} by user id {}", bookingId, userId);
        return bookingService.findBookingById(bookingId, userId);
    }

    /**
     * Получение всех броннирований пользователя с параметром
     * @param userId booker ID
     * @param state state
     * @return list of Bookings
     */
    @GetMapping
    public List<Booking> findAllByBooker(@RequestHeader(ownerIdHeaderTitle) long userId,
                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                         @RequestParam(required = false, defaultValue = "0") long from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Getting {} booker bookings", state);
        return bookingService.findAllByBooker(userId, state, from, size);
    }

    /**
     * Получение всех бронирований по владельцу вещи с параметром
     * @param userId owner ID
     * @param state state
     * @return list of Bookings
     */
    @GetMapping("/owner")
    public List<Booking> findAllByOwner(@RequestHeader(ownerIdHeaderTitle) long userId,
                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestParam(required = false, defaultValue = "0") long from,
                                        @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Getting {} owner bookings", state);
        return bookingService.findAllByOwner(userId, state, from, size);
    }
}

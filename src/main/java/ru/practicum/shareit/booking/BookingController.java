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

    @PostMapping
    public Booking addBooking(@RequestBody BookingDto bookingDto,
                              @RequestHeader(ownerIdHeaderTitle) long booker) {
        log.info("Adding new booking for item id: " + bookingDto.getItemId() + " by booker id: " + booker);
        return bookingService.addBooking(bookingDto, booker);
    }

    @PatchMapping("/{bookingId}")
    public Booking setOwnersDecision(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(ownerIdHeaderTitle) long ownerId) {
        log.info("Owner id: " + ownerId + " chooses decision");
        return bookingService.setOwnersDecision(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public Booking findBookingById(@PathVariable long bookingId,
                                   @RequestHeader(ownerIdHeaderTitle) long userId) {
        log.info("Getting booking id: " + bookingId + " by user id " + userId);
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> findAllByBooker(@RequestHeader(ownerIdHeaderTitle) long userId,
                                         @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Getting " + state + " booker bookings");
        return bookingService.findAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> findAllByOwner(@RequestHeader(ownerIdHeaderTitle) long userId,
                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Getting " + state + " owner bookings");
        return bookingService.findAllByOwner(userId, state);
    }
}

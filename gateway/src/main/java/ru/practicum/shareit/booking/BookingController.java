package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private final String ownerIdHeaderTitle = "x-sharer-user-id";

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestBody BookingDto bookingDto,
											 @RequestHeader(ownerIdHeaderTitle) long booker) {
		log.info("Adding new booking for item id: {} by booker id: {}", bookingDto.getItemId(), booker);
		return bookingClient.addBooking(bookingDto, booker);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setOwnersDecision(@PathVariable long bookingId,
									 @RequestParam boolean approved,
									 @RequestHeader(ownerIdHeaderTitle) long ownerId) {
		log.info("Owner id: {} chooses decision", ownerId);
		return bookingClient.setOwnersDecision(bookingId, approved, ownerId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findBookingById(@PathVariable long bookingId,
								   @RequestHeader(ownerIdHeaderTitle) long userId) {
		log.info("Getting booking id: {} by user id {}", bookingId, userId);
		return bookingClient.findBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByBooker(@RequestHeader(ownerIdHeaderTitle) long userId,
										 @RequestParam(required = false, defaultValue = "ALL") String state,
										 @RequestParam(required = false, defaultValue = "0") long from,
										 @RequestParam(required = false, defaultValue = "20") int size) {
		log.info("Getting {} booker bookings", state);
		return bookingClient.findAllByBooker(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllByOwner(@RequestHeader(ownerIdHeaderTitle) long userId,
										@RequestParam(required = false, defaultValue = "ALL") String state,
										@RequestParam(required = false, defaultValue = "0") long from,
										@RequestParam(required = false, defaultValue = "20") int size) {
		log.info("Getting {} owner bookings", state);
		return bookingClient.findAllByOwner(userId, state, from, size);
	}
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingDto booking,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(booking, userId);
    }

    @DeleteMapping("/{bookingId}")
    public BookingDto deleteBooking(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.deleteBooking(bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        return bookingService.setApprove(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getBookingForCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingForCurrentUser(new RequestBooking(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingForOwner(new RequestBooking(userId, state, from, size));
    }
}
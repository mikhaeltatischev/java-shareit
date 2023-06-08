package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody Booking booking,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(booking, userId);
    }

    @DeleteMapping("/{bookingId}")
    public BookingDto deleteBooking(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.deleteBooking(bookingId, userId);
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping
    public List<BookingDto> getBookings() {
        return bookingService.getBookings();
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@Valid @RequestBody Booking booking,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        return bookingService.updateBooking(booking, userId, bookingId);
    }

    @PatchMapping("/confirmation/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId) {
        return bookingService.confirmationBooking(userId, bookingId);
    }
}

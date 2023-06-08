package ru.practicum.shareit.booking.repostitory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository {

    BookingDto addBooking(Booking booking, Long userId);

    BookingDto deleteBooking(Long bookingId, Long userId);

    BookingDto getBookingById(Long id);

    List<BookingDto> getBookings();

    BookingDto updateBooking(Booking booking, Long userId, Long bookingId);

    BookingDto confirmationBooking(Long userId, Long bookingId);
}

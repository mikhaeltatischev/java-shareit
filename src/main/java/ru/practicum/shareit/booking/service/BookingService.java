package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBooking;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingDto booking, Long userId);

    BookingDto deleteBooking(Long bookingId, Long userId);

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto setApprove(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getBookingForCurrentUser(RequestBooking requestBooking);

    List<BookingDto> getBookingForOwner(RequestBooking requestBooking);
}

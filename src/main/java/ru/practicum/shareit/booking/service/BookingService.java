package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.GetBooking;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingDto booking, Long userId);

    BookingDto deleteBooking(Long bookingId, Long userId);

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto setApprove(Long userId, Long bookingId, String approved);

    List<BookingDto> getBookingForCurrentUser(GetBooking getBooking);

    List<BookingDto> getBookingForOwner(GetBooking getBooking);
}

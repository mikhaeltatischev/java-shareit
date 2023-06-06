package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;

    @Override
    public BookingDto addBooking(Booking booking, Long userId) {
        itemService.getItemById(booking.getItemId());
        return bookingRepository.addBooking(booking, userId);
    }

    @Override
    public BookingDto deleteBooking(Long bookingId, Long userId) {
        return bookingRepository.deleteBooking(bookingId, userId);
    }

    @Override
    public BookingDto getBookingById(Long id) {
        return bookingRepository.getBookingById(id);
    }

    @Override
    public List<BookingDto> getBookings() {
        return bookingRepository.getBookings();
    }

    @Override
    public BookingDto updateBooking(Booking booking, Long userId, Long bookingId) {
        return bookingRepository.updateBooking(booking, userId, bookingId);
    }
}

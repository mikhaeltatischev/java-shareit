package ru.practicum.shareit.booking.repostitory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.exception.NotOwnerException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

    private static Long bookingId = 1L;
    private final List<Booking> bookings = new ArrayList<>();

    @Override
    public BookingDto addBooking(Booking booking, Long userId) {
        booking.setId(bookingId++);
        booking.setAuthor(userId);
        bookings.add(booking);
        log.info("Booking: " + booking + " saved");

        return createBookingDto(booking);
    }

    @Override
    public BookingDto deleteBooking(Long bookingId, Long userId) {
        Booking booking = findBooking(bookingId);
        checkOwnerId(booking, userId);
        bookings.remove(booking);
        log.info("Booking: " + booking + " deleted");

        return createBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long id) {
        Booking booking = findBooking(id);
        log.info("Booking with id: " + id + ": " + booking);

        return createBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings() {
        List<BookingDto> bookingsDto = new ArrayList<>();

        bookings.forEach(booking -> bookingsDto.add(createBookingDto(booking)));

        return bookingsDto;
    }

    @Override
    public BookingDto updateBooking(Booking booking, Long userId, Long bookingId) {
        Booking currentBooking = findBooking(bookingId);
        checkOwnerId(currentBooking, userId);

        if (booking.getItemId() != null) {
            currentBooking.setItemId(booking.getItemId());
        }

        if (booking.getStartBookingDate() != null) {
            currentBooking.setStartBookingDate(booking.getStartBookingDate());
        }

        if (booking.getEndBookingDate() != null) {
            currentBooking.setEndBookingDate(booking.getEndBookingDate());
        }

        log.info("Booking with id: " + bookingId + " updated");

        return createBookingDto(currentBooking);
    }

    @Override
    public BookingDto confirmationBooking(Long userId, Long bookingId) {
        Booking booking = findBooking(bookingId);
        checkOwnerId(booking, userId);
        booking.setConfirmed(true);
        log.info("Booking with id: " + bookingId + " confirmed from User with id: " + userId);

        return createBookingDto(booking);
    }

    private BookingDto createBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getAuthor(), booking.getItemId(), booking.getStartBookingDate(),
                booking.getEndBookingDate(), booking.getConfirmed());
    }

    private Booking findBooking(Long bookingId) {
        return bookings.stream()
                .filter(currentBooking -> currentBooking.getId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new BookingNotFoundException("Booking with id: " + bookingId + " not found"));
    }

    private void checkOwnerId(Booking booking, Long userId) {
        if (!booking.getAuthor().equals(userId)) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner booking with id: " + booking.getId());
        }
    }
}

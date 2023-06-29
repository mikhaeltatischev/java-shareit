package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(bookingDto.getItemId()));

        if (item.getUser() != null) {
            if (item.getUser().getUserId().equals(userId)) {
                throw new BookingCreateException(userId, item.getItemId());
            }
        }

        if (item.getAvailable().equals(false)) {
            throw new ItemNotAvailableException(item.getItemId());
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new BookingTimeException(bookingDto.getStart(), bookingDto.getEnd());
        }

        Booking booking = bookingRepository.save(toBooking(bookingDto, user, item));
        log.info("Saved: " + booking);

        return toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto deleteBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getUser().getUserId().equals(userId)) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner Booking with id: " + bookingId);
        }

        bookingRepository.deleteById(bookingId);
        log.info("Booking with id: " + bookingId + " removed");

        return toDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!(booking.getItem().getUser().getUserId().equals(userId) || booking.getUser().getUserId().equals(userId))) {
            throw new NotOwnerException("User with id: " + userId + " don't have access to Booking with id: " + bookingId);
        }

        log.info("Found booking: " + booking);

        return toDto(booking);
    }

    @Override
    public BookingDto setApprove(Long userId, Long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingAlreadyApprovedException(bookingId);
        }

        if (!booking.getItem().getUser().getUserId().equals(userId)) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner Item with id: "
                    + booking.getItem().getItemId());
        }

        if (approved.equals("true")) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        log.info("Changed status to " + approved + ", booking with id: " + bookingId);

        return toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingForCurrentUser(Long userId, String state) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByUserUserIdOrderByEndTimeDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(userId, time, time);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, time);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(userId, time);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, time, Status.valueOf(state));
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByUserUserIdAndStatusOrderByEndTimeDesc(userId, Status.valueOf(state));
                break;
            default:
                throw new NotValidStateException("Unknown state: " + state);
        }
        return toDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingForOwner(Long userId, String state) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllBookingByOwnerId(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(userId, time, time);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, time);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(userId, time);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, time, Status.valueOf(state));
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(userId, Status.valueOf(state));
                break;
            default:
                throw new NotValidStateException("Unknown state: " + state);
        }
        return toDto(bookings);
    }
}

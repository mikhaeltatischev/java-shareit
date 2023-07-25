package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.common.NotOwnerException;
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

        if (item.getUser() != null && userId.equals(item.getUser().getUserId())) {
            throw new BookingCreateException(userId, item.getItemId());
        }

        if (!item.getAvailable()) {
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

        if (!userId.equals(booking.getUser().getUserId())) {
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

        if (!(userId.equals(booking.getItem().getUser().getUserId()) || userId.equals(booking.getUser().getUserId()))) {
            throw new NotOwnerException("User with id: " + userId + " don't have access to Booking with id: " + bookingId);
        }

        log.info("Found booking: " + booking);

        return toDto(booking);
    }

    @Override
    public BookingDto setApprove(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));


        if (!userId.equals(booking.getItem().getUser().getUserId())) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner Item with id: "
                    + booking.getItem().getItemId());
        }

        if (approved && Status.APPROVED.equals(booking.getStatus())) {
            throw new BookingAlreadyApprovedException(bookingId);
        } else if (!approved) {
            booking.setStatus(Status.REJECTED);
        } else {
            booking.setStatus(Status.APPROVED);
        }

        bookingRepository.save(booking);
        log.info("Changed status to " + booking.getStatus() + ", booking with id: " + bookingId);

        return toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingForCurrentUser(RequestBooking requestBooking) {
        Long userId = requestBooking.getUserId();
        String state = requestBooking.getState().toUpperCase();
        PageRequest pageRequest = PageRequest.of(requestBooking.getFrom() / requestBooking.getSize(), requestBooking.getSize());
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByUserUserIdOrderByEndTimeDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(userId, time, time, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, time, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(userId, time, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, time, Status.valueOf(state), pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByUserUserIdAndStatusOrderByEndTimeDesc(userId, Status.valueOf(state), pageRequest);
                break;
            default:
                throw new NotValidStateException("Unknown state: " + state);
        }
        return toDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingForOwner(RequestBooking requestBooking) {
        Long userId = requestBooking.getUserId();
        String state = requestBooking.getState().toUpperCase();
        PageRequest pageRequest = PageRequest.of(requestBooking.getFrom() / requestBooking.getSize(), requestBooking.getSize());
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllBookingByOwnerId(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(userId, time, time, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, time, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(userId, time, pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, time, Status.valueOf(state), pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(userId, Status.valueOf(state), pageRequest);
                break;
            default:
                throw new NotValidStateException("Unknown state: " + state);
        }
        return toDto(bookings);
    }
}
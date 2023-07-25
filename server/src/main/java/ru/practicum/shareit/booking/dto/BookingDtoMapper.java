package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDtoMapper {

    public static BookingDto toDto(Booking booking) {
        Long id = booking.getBookingId();
        User user = booking.getUser();
        Item item = booking.getItem();
        Long itemId = booking.getItem().getItemId();
        String itemName = booking.getItem().getName();
        LocalDateTime start = booking.getStartTime();
        LocalDateTime end = booking.getEndTime();
        Status status = booking.getStatus();

        return new BookingDto(id, UserDtoMapper.toDto(user), user.getUserId(), ItemDtoMapper.toDto(item),
                itemId, itemName, start, end, status);
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        Long id = bookingDto.getId();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        Status status = bookingDto.getStatus();

        return new Booking(id, user, item, start, end, status);
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingDtos.add(toDto(booking));
        }

        return bookingDtos;
    }
}

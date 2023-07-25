package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private UserDto booker;
    private Long bookerId;
    private ItemDto item;
    private Long itemId;
    private String itemName;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status = Status.WAITING;
}

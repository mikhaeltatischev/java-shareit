package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBookingDto {

    private Long id;
    private String name;
    private UserDto user;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

    public ItemBookingDto(Long id, String name, UserDto user, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.description = description;
        this.available = available;
    }

    public ItemBookingDto(Long id, String name, UserDto user, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}

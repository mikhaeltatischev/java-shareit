package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    private Long id;
    private Long author;
    private Long itemId;
    @Future
    private LocalDateTime startBookingDate;
    @Future
    private LocalDateTime endBookingDate;
    private Boolean confirmed;
}

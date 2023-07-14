package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class requestBooking {

    private Long userId;
    private String state;
    private int from;
    private int size;
}

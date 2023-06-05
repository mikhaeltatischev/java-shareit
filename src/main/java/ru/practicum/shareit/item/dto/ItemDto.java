package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.RentStatus;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;
    private final String owner;
    private String name;
    private String description;
    private RentStatus rentStatus;
}

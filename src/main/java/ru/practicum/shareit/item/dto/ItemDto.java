package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
}

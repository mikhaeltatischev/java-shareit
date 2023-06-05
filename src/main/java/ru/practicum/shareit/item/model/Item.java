package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.item.RentStatus;

@Data
public class Item {

    private Long id;
    private final String owner;
    private String name;
    private String description;
    private RentStatus rentStatus;
}

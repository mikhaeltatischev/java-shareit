package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {

    private Long id;
    private String itemName;
    private Long authorId;
    private LocalDateTime timeOfCreation;
}

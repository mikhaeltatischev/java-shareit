package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ItemRequestDto {

    private Long id;
    private String itemName;
    private Long authorId;
    private LocalDateTime timeOfCreation;
}

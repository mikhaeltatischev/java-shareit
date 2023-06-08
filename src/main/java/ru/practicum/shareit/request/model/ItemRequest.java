package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ItemRequest {

    private Long id;
    @NotBlank
    private String itemName;
    private Long authorId;
    private LocalDateTime timeOfCreation;
}

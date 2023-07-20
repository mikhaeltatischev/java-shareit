package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {


    @NotBlank
    private String description;
}

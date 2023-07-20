package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class CommentDto {

    @NotBlank
    private String text;
}

package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserDto {

    private Long id;
    @NotBlank
    private String name;
    @Email(message = "Incorrect email")
    @NotBlank
    private String email;
}

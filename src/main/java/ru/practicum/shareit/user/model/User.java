package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    private Long id;
    @NotBlank
    private String name;
    @Email(message = "Incorrect email")
    @NotBlank
    private String email;
    private List<String> reviews;
}

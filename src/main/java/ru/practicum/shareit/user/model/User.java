package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    private Long id;
    private String name;
    private List<String> reviews;
}

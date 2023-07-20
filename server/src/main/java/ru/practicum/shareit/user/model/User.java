package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = {"userId"})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String name;

    @Column(unique = true)
    private String email;
}
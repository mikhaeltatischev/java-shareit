package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @ToString.Exclude
    private User author;

    @Column
    private Integer rating;

    @Column
    private String text;

    @Column
    private LocalDateTime timeOfCreation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId")
    @ToString.Exclude
    private Item item;
}

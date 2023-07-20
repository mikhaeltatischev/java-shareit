package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDtoMapper {

    public static CommentDto toDto(Comment comment) {
        Long commentId = comment.getCommentId();
        User author = comment.getAuthor();
        Long authorId = comment.getAuthor().getUserId();
        int rating = comment.getRating();
        String text = comment.getText();
        LocalDateTime timeOfCreation = comment.getTimeOfCreation();
        Item item = comment.getItem();
        Long itemId = comment.getItem().getItemId();

        return new CommentDto(commentId, UserDtoMapper.toDto(author), author.getName(), authorId, rating, text, timeOfCreation, ItemDtoMapper.toDto(item), itemId);
    }

    public static Comment toComment(CommentDto comment, User user, Item item) {
        Long commentId = comment.getId();
        int rating = comment.getRating();
        String text = comment.getText();
        LocalDateTime timeOfCreation = comment.getCreated();

        return new Comment(commentId, user, rating, text, timeOfCreation, item);
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment : comments) {
            commentDtos.add(toDto(comment));
        }

        return commentDtos;
    }
}

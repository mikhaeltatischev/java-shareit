package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CommentRepositoryIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    private long itemId;

    @BeforeEach
    public void setUp() {
        itemId = 1L;
    }

    @Test
    public void findAllCommentByItemItemIdWhenItemHasOneCommentReturnOneComment() {
        List<Comment> comments = commentRepository.findAllCommentByItemItemId(itemId);

        assertEquals(1, comments.size());
    }

    @Test
    public void findAllCommentByItemItemIdWhenItemDoesntHasCommentsReturnEmptyList() {
        long itemIdWithoutComments = 2L;

        List<Comment> comments = commentRepository.findAllCommentByItemItemId(itemIdWithoutComments);

        assertEquals(0, comments.size());
    }

    @Test
    public void findAllByItemsWhenReturn() {
        List<Item> items = itemRepository.findAll();

        List<Comment> comments = commentRepository.findAllByItems(items);

        assertEquals(1, comments.size());
    }
}
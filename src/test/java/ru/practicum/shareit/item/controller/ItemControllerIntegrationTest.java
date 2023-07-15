package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(
        properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemControllerIntegrationTest {

    private static final String URL = "/items";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private long userId;
    private long itemId;
    private long newUserId;
    private long unknownUserId;
    private long unknownItemId;
    private long commentId;
    private ItemBookingDto itemBookingDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDtoNew;
    private CommentDto commentDto;

    @BeforeEach
    public void setUp() {
        unknownUserId = 100L;
        unknownItemId = 100L;
        userId = 1L;
        newUserId = 2L;
        itemId = 1L;
        commentId = 1L;

        userDto = UserDto.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Jon Bon")
                .build();

        userDtoNew = UserDto.builder()
                .id(newUserId)
                .name("Bon Jon")
                .email("google@mail.com")
                .build();

        itemBookingDto = ItemBookingDto.builder()
                .id(itemId)
                .available(true)
                .name("Отвертка")
                .user(userDto)
                .description("Простая отвертка")
                .requestId(1L)
                .build();

        itemDto = ItemDto.builder()
                .id(itemId)
                .user(userDto)
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true)
                .requestId(1L)
                .build();

        commentDto = CommentDto.builder()
                .item(itemDto)
                .author(userDtoNew)
                .created(LocalDateTime.now())
                .rating(1)
                .text("text")
                .authorName("Bon Jon")
                .build();
    }

    @Test
    @SneakyThrows
    public void getItemByIdWhenMethodInvokedReturnItem() {
        mvc.perform(get(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.name", is(itemBookingDto.getName())))
                .andExpect(jsonPath("$.user.id", is((int) userId)))
                .andExpect(jsonPath("$.description", is(itemBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemBookingDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void getItemByIdWhenItemNotFoundReturnStatusNotFound() {
        mvc.perform(get(URL + "/{itemId}", unknownItemId)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeReturnListWithThreeItems() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeItemsAndSizeIsOneReturnListWithOneItem() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-id", userId)
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenFoundThreeItemsAndFromIsOneAndSizeIs2ReturnListWithTwoItems() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-id", userId)
                        .queryParam("size", "2")
                        .queryParam("from", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getItemsForUserWhenItemsNotFoundReturnEmptyList() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-id", newUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenNameChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .name("Топор")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.name", is(updateItem.getName())))
                .andExpect(jsonPath("$.user.id", is((int) userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenNameChangedAndNameIsBlankReturnStatusBadRequest() {
        ItemDto updateItem = ItemDto.builder()
                .name("")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateItemWhenDescriptionChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .description("Простая отвертка")
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.user.id", is((int) userId)))
                .andExpect(jsonPath("$.description", is(updateItem.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenAvailableChangedReturnUpdatedItem() {
        ItemDto updateItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.user.id", is((int) userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItem.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void updateItemWhenItemNotFoundReturnStatusIsNotFound() {
        ItemDto updateItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch(URL + "/{itemId}", unknownItemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void updateItemWhenUserInNotOwnerTheItemReturnStatusIsNotFound() {
        ItemDto updateItem = ItemDto.builder()
                .available(false)
                .build();

        mvc.perform(patch(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", newUserId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addItemWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(post(URL)
                        .header("X-Sharer-User-id", unknownUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addItemWhenNameIsBlankReturnStatusIsBadRequest() {
        itemDto.setName("");

        mvc.perform(post(URL)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addItemWhenDescriptionIsBlankReturnStatusIsBadRequest() {
        itemDto.setDescription("");

        mvc.perform(post(URL)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addItemWhenRequestIdIsNegativeReturnStatusIsBadRequest() {
        itemDto.setRequestId(-1L);

        mvc.perform(post(URL)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenInvokedMethodReturnItem() {
        mvc.perform(delete(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.user.id", is((int) userId)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenItemNotFoundReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{itemId}", unknownItemId)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", 2))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItemWhenUserIsNotOwnerTheItemReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{itemId}", itemId)
                        .header("X-Sharer-User-id", newUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void searchWhenFoundOneItemReturnListWithOneItem() {
        mvc.perform(get(URL + "/search")
                        .header("X-Sharer-User-id", userId)
                        .queryParam("text", "Отвертка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is((int) itemId)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].user.id", is((int) userId)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void searchWhenNotFoundItemsReturnEmptyList() {
        mvc.perform(get(URL + "/search")
                        .header("X-Sharer-User-id", userId)
                        .queryParam("text", "Стремянка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void searchWhenFoundTwoItemsAndSizeIsOneReturnListWithOneItem() {
        mvc.perform(get(URL + "/search")
                        .header("X-Sharer-User-id", userId)
                        .queryParam("text", "Отвертка")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is((int) itemId)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].user.id", is((int) userId)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void addCommentWhenInvokedMethodReturnComment() {
        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header("X-Sharer-User-id", newUserId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) commentId)))
                .andExpect(jsonPath("$.authorId", is((int) newUserId)))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    @SneakyThrows
    public void addCommentWhenUserIsOwnerTheItemReturnStatusIs() {
        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header("X-Sharer-User-id", userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addCommentWhenUserDoesntHaveBookingTheItemReturnStatusIs() {
        long userIdWithoutBooking = 3L;

        mvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header("X-Sharer-User-id", userIdWithoutBooking)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
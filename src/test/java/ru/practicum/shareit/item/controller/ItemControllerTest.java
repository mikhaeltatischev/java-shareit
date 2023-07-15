package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentCreateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private ItemBookingDto itemBookingDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private long userId;
    private long itemId;

    @BeforeEach
    public void fillItems() {
        userId = 1L;
        itemId = 1L;
        userDto = new UserDto(userId, "Jon Bon", "mail@mail.ru");
        itemBookingDto = new ItemBookingDto(itemId, "Отвертка", userDto, "Классная отвертка", true);
        itemDto = new ItemDto(itemId, "Стремянка", userDto, "Высокая стремянка", true, 1L);
    }

    @Test
    public void getItemByIdWhenMethodInvokedReturnItem() {
        when(itemService.getItemById(itemId, userId)).thenReturn(itemBookingDto);

        assertEquals(itemBookingDto, controller.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemNotFoundThrowException() {
        when(itemService.getItemById(itemId, userId)).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> controller.getItemById(itemId, userId));
    }

    @Test
    public void getItemsForUserWhenFoundOneItemReturnListWithOneItem() {
        List<ItemBookingDto> items = List.of(itemBookingDto);
        RequestItem requestItem = RequestItem.of(userId, 0, 10);

        when(itemService.getItemsForUser(requestItem)).thenReturn(items);

        List<ItemBookingDto> foundItems = controller.getItemsForUser(userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void getItemsForUserWhenItemNotFoundReturnEmptyList() {
        List<ItemBookingDto> items = List.of();
        RequestItem requestItem = RequestItem.of(userId, 0, 10);

        when(itemService.getItemsForUser(requestItem)).thenReturn(items);

        List<ItemBookingDto> foundItems = controller.getItemsForUser(userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void updateItemWhenInvokedMethodReturnUpdatedItem() {
        ItemDto newItem = new ItemDto(1L, "Updated Стремянка", userDto, "Updated Высокая стремянка", false, 2L);

        when(itemService.updateItem(newItem, userId, itemId)).thenReturn(newItem);

        ItemDto updatedItem = controller.updateItem(itemId, newItem, userId);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getId(), updatedItem.getId());
        assertEquals(newItem.getName(), updatedItem.getName());
        assertEquals(newItem.getUser(), updatedItem.getUser());
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        assertEquals(newItem.getRequestId(), updatedItem.getRequestId());
    }

    @Test
    public void updateItemWhenItemNotFoundThrowException() {
        ItemDto newItem = new ItemDto(1L, "Updated Стремянка", userDto, "Updated Высокая стремянка", false, 2L);

        when(itemService.updateItem(newItem, userId, itemId)).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> controller.updateItem(itemId, newItem, userId));
    }

    @Test
    public void addItemWhenMethodInvokedReturnItem() {
        when(itemService.addItem(itemDto, userId)).thenReturn(itemDto);

        assertEquals(itemDto, controller.addItem(itemDto, userId));
        verify(itemService).addItem(itemDto, userId);
    }

    @Test
    public void deleteItemWhenInvokedMethodReturnItem() {
        when(itemService.deleteItem(itemId, userId)).thenReturn(itemDto);

        assertEquals(itemDto, controller.deleteItem(itemId, userId));
        verify(itemService).deleteItem(itemId, userId);
    }

    @Test
    public void deleteItemWhenItemNotFoundThrowException() {
        when(itemService.deleteItem(itemId, userId)).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> controller.deleteItem(itemId, userId));
        verify(itemService).deleteItem(itemId, userId);
    }

    @Test
    public void searchWhenInvokedMethodReturnOneItem() {
        List<ItemDto> items = List.of(itemDto);
        RequestItem requestItem = RequestItem.of(userId, 0, 10, "text");

        when(itemService.searchItem(requestItem)).thenReturn(items);

        List<ItemDto> foundItems = controller.search("text", userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void searchWhenItemsNotFoundReturnEmptyList() {
        List<ItemDto> items = List.of();
        RequestItem requestItem = RequestItem.of(userId, 0, 10, "text");

        when(itemService.searchItem(requestItem)).thenReturn(items);

        List<ItemDto> foundItems = controller.search("text", userId, 0, 10);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void addCommentWhenMethodInvokedReturnComment() {
        CommentDto commentDto = new CommentDto(1L, userDto, "Name", 1L, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemService.addComment(commentDto, userId, itemId)).thenReturn(commentDto);

        assertEquals(commentDto, controller.addComment(commentDto, userId, itemId));
    }

    @Test
    public void addCommentWhenUserIsOwnerTheItemThrowException() {
        CommentDto commentDto = new CommentDto(1L, userDto, "Name", 1L, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemService.addComment(commentDto, userId, itemId)).thenThrow(CommentCreateException.class);

        assertThrows(CommentCreateException.class, () -> controller.addComment(commentDto, userId, itemId));
    }
}
package ru.practicum.shareit.request.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    private static final String URL = "/requests";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private long itemRequestId;
    private long userId;
    private ItemRequestDto itemRequest;

    @BeforeEach
    public void setUp() {
        itemRequestId = 1L;
        userId = 1L;

        itemRequest = ItemRequestDto.builder()
                .id(itemRequestId)
                .authorId(userId)
                .description("Отвертка")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenInvokedMethodReturnRequest() {
        when(itemRequestService.addItemRequest(itemRequest, userId)).thenReturn(itemRequest);

        mvc.perform(post(URL)
                    .header("X-Sharer-User-Id", userId)
                    .content(mapper.writeValueAsString(itemRequest))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemRequestId)))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    public void addItemRequestWhenUserNotFoundReturnStatusIsNotFound() {
        when(itemRequestService.addItemRequest(itemRequest, userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenInvokedMethodReturnOneRequest() {
        List<ItemRequestDto> requests = List.of(itemRequest);

        when(itemRequestService.getItemRequestsForUser(userId)).thenReturn(requests);

        mvc.perform(get(URL)
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenInvokedMethodReturnEmptyList() {
        List<ItemRequestDto> requests = List.of();

        when(itemRequestService.getItemRequestsForUser(userId)).thenReturn(requests);

        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsForUserWhenUserNotFoundReturnStatusIsNotFound() {
        when(itemRequestService.getItemRequestsForUser(userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenInvokedMethodReturnOneBooking() {
        List<ItemRequestDto> requests = List.of(itemRequest);

        when(itemRequestService.getItemRequests(any())).thenReturn(requests);

        mvc.perform(get(URL + "/all")
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestsPageableWhenInvokedMethodReturnEmptyList() {
        List<ItemRequestDto> requests = List.of();

        when(itemRequestService.getItemRequests(any())).thenReturn(requests);

        mvc.perform(get(URL + "/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenInvokedMethodReturnRequest() {
        when(itemRequestService.getItemRequestById(itemRequestId, userId)).thenReturn(itemRequest);

        mvc.perform(get(URL + "/{requestId}", itemRequestId)
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemRequestId)))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    public void getItemRequestByIdWhenRequestNotFoundReturnStatusIsNotFound() {
        when(itemRequestService.getItemRequestById(itemRequestId, userId)).thenThrow(ItemRequestNotFoundException.class);

        mvc.perform(get(URL + "/{requestId}", itemRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenInvokedMethodReturnRequest() {
        when(itemRequestService.deleteItemRequest(itemRequestId, userId)).thenReturn(itemRequest);

        mvc.perform(delete(URL + "/{requestId}", itemRequestId)
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemRequestId)))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    public void deleteItemRequestWhenRequestNotFoundReturnStatusIsNotFound() {
        when(itemRequestService.deleteItemRequest(itemRequestId, userId)).thenThrow(ItemRequestNotFoundException.class);

        mvc.perform(delete(URL + "/{requestId}", itemRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }
}
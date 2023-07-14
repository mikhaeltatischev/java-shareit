package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    private static final String URL = "/bookings";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private RequestBooking requestBooking;
    private long userId;
    private long bookingId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        bookingId = 1L;

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .itemName("Отвертка")
                .bookerId(userId)
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        requestBooking = requestBooking.builder()
                .userId(1L)
                .state("true")
                .size(10)
                .from(0)
                .build();
    }

    @Test
    @SneakyThrows
    public void addBookingWhenInvokedMethodReturnBooking() {
        when(bookingService.addBooking(bookingDto, userId)).thenReturn(bookingDto);

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenInvokedMethodReturnStatusIsNotFound() {
        when(bookingService.addBooking(bookingDto, userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenInvokedMethodReturnBooking() {
        when(bookingService.deleteBooking(bookingId, userId)).thenReturn(bookingDto);

        mvc.perform(delete(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenBookingNotFoundReturnStatusIsNotFound() {
        when(bookingService.deleteBooking(bookingId, userId)).thenThrow(BookingNotFoundException.class);

        mvc.perform(delete(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWhenInvokedMethodReturnBooking() {
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(bookingDto);

        mvc.perform(get(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenInvokedMethodReturnBooking() {
        when(bookingService.setApprove(userId, bookingId, true)).thenReturn(bookingDto);

        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenBookingNotFoundReturnStatusIsNotFound() {
        when(bookingService.setApprove(userId, bookingId, true)).thenThrow(BookingNotFoundException.class);

        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenInvokedMethodReturnOneBooking() {
        when(bookingService.getBookingForCurrentUser(requestBooking)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenInvokedMethodReturnOneBooking() {
        when(bookingService.getBookingForOwner(requestBooking)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}
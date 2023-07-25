package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(
        properties = "db.name = test"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingControllerIntegrationTest {

    private static final String URL = "/bookings";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private LocalDateTime now;
    private long createBookingId;
    private long bookingId;
    private long itemId;
    private long userId;
    private long itemOwnerId;
    private long unknownUserId;
    private long unknownItemId;
    private long unknownBookingId;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.now();
        createBookingId = 2L;
        itemOwnerId = 1L;
        userId = 2L;
        itemId = 1L;
        unknownUserId = 100L;
        unknownItemId = 100L;
        unknownBookingId = 100L;
        bookingId = 1L;

        bookingDto = BookingDto.builder()
                .id(createBookingId)
                .itemId(itemId)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .build();
    }

    @Test
    @SneakyThrows
    public void addBookingWhenInvokedMethodReturnBooking() {
        mvc.perform(post(URL)
                    .header("X-Sharer-User-Id", userId)
                    .content(mapper.writeValueAsString(bookingDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookerId", is((int) userId)));
    }

    @Test
    @SneakyThrows
    public void addBookingWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", unknownUserId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenItemNotFoundReturnStatusIsNotFound() {
        bookingDto.setItemId(unknownItemId);

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
    public void addBookingWhenEndTimeInThePastReturnStatusIsBadRequest() {
        bookingDto.setEnd(now.minusDays(2));

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenItemNotAvailableReturnStatusIsBadRequest() {
        long notAvailableItemId = 3L;
        bookingDto.setItemId(notAvailableItemId);

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenUserIsTheOwnerOfTheItemReturnStatusIsNotFound() {
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenStartTimeIsAfterEndTimeReturnStatusIsBadRequest() {
        bookingDto.setStart(now.plusDays(1));

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenInvokedMethodReturnBooking() {
        mvc.perform(delete(URL + "/{bookingId}", 1)
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookerId", is((int) userId)));
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenBookingNotFoundReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{bookingId}", unknownBookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenUserNotOwnerTheBookingReturnStatusIsNotFound() {
        mvc.perform(delete(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", itemOwnerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWhenInvokedMethodReturnBooking() {
        mvc.perform(get(URL + "/{bookingId}", bookingId)
                    .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookerId", is((int) userId)));
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWhenBookingNotFoundReturnStatusIsNotFound() {
        mvc.perform(get(URL + "/{bookingId}", unknownBookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWhenUserDoesntHaveAccessReturnStatusIsNotFound() {
        long userWithoutAccess = 3L;

        mvc.perform(get(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userWithoutAccess))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenInvokedMethodReturnBooking() {
        long notApprovedBookingId = 4L;

        mvc.perform(patch(URL + "/{bookingId}", notApprovedBookingId)
                    .header("X-Sharer-User-Id", itemOwnerId)
                    .queryParam("approved", "true")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenBookingAlreadyApprovedReturnStatusIsBadRequest() {
        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenUserIsNotOwnerTheItemReturnStatusIsNotFound() {
        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenBookingNotFoundReturnStatusIsNotFound() {
        mvc.perform(patch(URL + "/{bookingId}", unknownBookingId)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenStateIsRejectedReturnBooking() {
        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsAllReturnFourBookings() {
        mvc.perform(get(URL)
                    .header("X-Sharer-User-Id", userId)
                    .queryParam("from", "0")
                    .queryParam("size", "10")
                    .queryParam("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenSizeIsTwoReturnTwoBookings() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "2")
                        .queryParam("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", unknownUserId)
                        .queryParam("from", "0")
                        .queryParam("size", "2")
                        .queryParam("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsCurrentReturnOneBooking() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsPastReturnOneBooking() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "Past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsFutureReturnTwoBookings() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsWaitingReturnEmptyList() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "waiting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsRejectedReturnOneBooking() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "rejected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenStateIsUnknownReturnOneBooking() {
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "Unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenStateIsAllReturnFourBookings() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)));
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenSizeIsTwoReturnTwoBookings() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "2")
                        .queryParam("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenUserNotFoundReturnStatusIsNotFound() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", unknownUserId)
                        .queryParam("from", "0")
                        .queryParam("size", "2")
                        .queryParam("state", "ALL"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"past", "current"})
    public void getBookingForOwnerWhenStateIsPastReturnOneBooking() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "Past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenStateIsFutureReturnTwoBookings() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"waiting", "rejected"})
    public void getBookingForOwnerWhenStateIsStringsReturnEmptyList(String state) {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenStateIsUnknownReturnOneBooking() {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .queryParam("state", "Unknown"))
                .andExpect(status().isBadRequest());
    }
}
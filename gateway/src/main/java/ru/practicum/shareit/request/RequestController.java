package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequest,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create request {}", itemRequest);
        return client.add(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests with userId = {}", userId);
        return client.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsPageable(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get requests with userId = {}, from = {}, size = {}", userId, from, size);
        return client.getItemRequestsPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @Positive @PathVariable Long requestId) {
        log.info("Get request {}, userId = {}", requestId, userId);
        return client.getItemRequest(userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteItemRequest(@Positive @PathVariable("requestId") Long requestId,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Delete request {}, userId = {}", requestId, userId);
        return client.delete(requestId, userId);
    }
}

package ru.practicum.shareit.request.model;

import lombok.Data;

@Data
public class GetItemRequest {
    private Long userId;
    private int from;
    private int size;

    public static GetItemRequest of(Long userId, int from, int size) {
        GetItemRequest request = new GetItemRequest();
        request.setUserId(userId);
        request.setFrom(from);
        request.setSize(size);

        return request;
    }
}

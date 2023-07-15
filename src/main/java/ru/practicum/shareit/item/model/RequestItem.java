package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestItem {

    private Long userId;
    private int from;
    private int size;
    private String text;

    public static RequestItem of(Long userId, int from, int size) {
        RequestItem item = new RequestItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);

        return item;
    }

    public static RequestItem of(Long userId, int from, int size, String text) {
        RequestItem item = new RequestItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);
        item.setText(text);

        return item;
    }
}

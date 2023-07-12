package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetItem {

    private Long userId;
    private int from;
    private int size;
    private String text;

    public static GetItem of(Long userId, int from, int size) {
        GetItem item = new GetItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);

        return item;
    }

    public static GetItem of(Long userId, int from, int size, String text) {
        GetItem item = new GetItem();
        item.setUserId(userId);
        item.setFrom(from);
        item.setSize(size);
        item.setText(text);

        return item;
    }
}

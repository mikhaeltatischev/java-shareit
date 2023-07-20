package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder
public class ItemRequestDto implements Comparable<ItemRequestDto> {

    private Long id;
    private String description;
    private Long authorId;
    private LocalDateTime created;
    private List<ItemResponseDto> items;

    public ItemRequestDto(Long id, String description, Long authorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.authorId = authorId;
        this.created = created;
    }

    @Override
    public int compareTo(ItemRequestDto request) {
        return request.getCreated().compareTo(this.created);
    }
}

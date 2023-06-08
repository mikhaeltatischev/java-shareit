package ru.practicum.shareit.request.dto;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ItemRequestDtoMapper implements RowMapper<ItemRequestDto> {

    @Override
    public ItemRequestDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String itemName = rs.getString("item_name");
        Long userId = rs.getLong("user_id");
        LocalDateTime timeOfCreation = rs.getTimestamp("time_of_creation").toLocalDateTime();

        return new ItemRequestDto(id, itemName, userId, timeOfCreation);
    }
}

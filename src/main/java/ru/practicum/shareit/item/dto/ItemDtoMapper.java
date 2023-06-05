package ru.practicum.shareit.item.dto;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.RentStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemDtoMapper implements RowMapper<ItemDto> {

    @Override
    public ItemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String owner = rs.getString("owner");
        String name = rs.getString("name");
        String description = rs.getString("description");
        RentStatus rentStatus = RentStatus.valueOf(rs.getString("rent_status"));

        return new ItemDto(id, owner, name, description, rentStatus);
    }
}

package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserUserId(Long userId, PageRequest pageRequest);

    List<Item> findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(boolean available,
                                                                                              String description,
                                                                                              String name,
                                                                                              PageRequest pageRequest);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i from Item as i " +
            "where i.requestId in ?1")
    List<Item> findAllByRequests(List<Long> requests);
}

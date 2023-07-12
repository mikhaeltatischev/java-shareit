package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemRepositoryIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private long userId;
    private int amountItems;
    private PageRequest defaultPageRequest;

    @BeforeEach
    public void setUp() {
        defaultPageRequest = PageRequest.of(0, 10);
        userId = 1L;
        amountItems = 3;
    }

    @Test
    public void findAllByUserIdWhenPageRequestDefaultReturnThreeItems() {
        assertEquals(amountItems, itemRepository.findAllByUserUserId(userId, defaultPageRequest).size());
    }

    @Test
    public void findAllByUserIdWhenPageRequestSizeIsTwoReturnTwoItems() {
        assertEquals(2, itemRepository.findAllByUserUserId(userId, PageRequest.of(0, 2)).size());
    }

    @Test
    public void findAllByUserIdWhenUserNotFoundReturnEmptyList() {
        long unknownUser = 100L;

        assertEquals(0, itemRepository.findAllByUserUserId(unknownUser, defaultPageRequest).size());
    }

    @Test
    public void findAllByUserIdWhenUserDontHaveItemsReturnEmptyList() {
        long userWithoutItems = 2L;

        assertEquals(0, itemRepository.findAllByUserUserId(userWithoutItems, defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenMethodInvokedReturnOneItem() {
        assertEquals(1, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "отвертка", "отвертка", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenOnlyDescriptionMatchReturnTwoItems() {
        assertEquals(2, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "прост", "прост", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenItemsNotFoundReturnEmptyList() {
        assertEquals(0, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "Бу", "Бу", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenAvailableFalseReturnOneItem() {
        assertEquals(1, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(false,
                        "Бенз", "Бенз", defaultPageRequest).size());
    }

    @Test
    public void findAllByRequestIdWhenInvokedMethodReturnTwoItems() {
        assertEquals(2, itemRepository.findAllByRequestId(1L).size());
    }

    @Test
    public void findAllByRequestIdWhenItemsNotFoundReturnEmptyList() {
        long unknownRequestId = 100L;

        assertEquals(0, itemRepository.findAllByRequestId(unknownRequestId).size());
    }

    @Test
    public void findAllByRequestsWhenItemsNotFoundByListReturnEmptyList() {
        long unknownRequestId = 100L;

        List<Long> requests = List.of(unknownRequestId);

        assertEquals(0, itemRepository.findAllByRequests(requests).size());
    }

    @Test
    public void findAllByRequestsWhenTwoItemsFoundReturnTwoItems() {
        long requestId = 1L;

        List<Long> requests = List.of(requestId);

        assertEquals(2, itemRepository.findAllByRequests(requests).size());
    }
}
package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemRequestRepositoryIntegrationTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private long userId;
    private long requestId;
    private long userWithoutRequests;

    @BeforeEach
    public void setUp() {
        userId = 2L;
        requestId = 1L;
        userWithoutRequests = 1L;
    }

    @Test
    public void getItemRequestsByUserWhenInvokedMethodReturnListItemRequests() {
        assertEquals(3, itemRequestRepository.getItemRequestsByUserUserId(userId).size());
    }

    @Test
    public void getItemRequestsByUserWhenItemRequestsNotFoundReturnEmptyList() {
        assertEquals(0, itemRequestRepository.getItemRequestsByUserUserId(userWithoutRequests).size());
    }

    @Test
    public void findAllByUserUserIdNotLikeWhenInvokedMethodReturnEmptyList() {
        assertEquals(0, itemRequestRepository.findAllByUserUserIdNotLike(userId, PageRequest.of(0,  10)).size());
    }

    @Test
    public void findAllByUserUserIdNotLikeWhenInvokedMethodReturnListWithThreeRequests() {
        assertEquals(3, itemRequestRepository.findAllByUserUserIdNotLike(userWithoutRequests, PageRequest.of(0,  10)).size());
    }
}
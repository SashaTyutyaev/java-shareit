package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @Test
    public void findOtherRequestsByRequestorIdSuccess() {
        Pageable pageable = Pageable.ofSize(10);
        user1 = createUser(1, "User1Name", "user1@mail.ru");
        User user2 = createUser(2, "User2Name", "user2@mail.ru");
        User user3 = createUser(3, "User3Name", "user3@mail.ru");
        request1 = createRequest(1, "Request1Description", user2);
        request2 = createRequest(2, "Request2Description", user1);
        request3 = createRequest(3, "Request3Description", user3);

        List<ItemRequest> requests = itemRequestRepository.findOtherRequestsByRequestorId(user1.getId(), pageable);
        assertEquals(2, requests.size());
        assertEquals(request1, requests.get(0));
        assertEquals(request3, requests.get(1));

        List<ItemRequest> newRequests = itemRequestRepository.findOtherRequestsByRequestorId(user2.getId(), pageable);
        assertEquals(2, newRequests.size());
        assertEquals(request2, newRequests.get(0));
        assertEquals(request3, newRequests.get(1));

        List<ItemRequest> newRequests2 = itemRequestRepository.findOtherRequestsByRequestorId(user3.getId(), pageable);
        assertEquals(2, newRequests2.size());
        assertEquals(request1, newRequests2.get(0));
        assertEquals(request2, newRequests2.get(1));
    }

    @Test
    public void findOtherRequestsByRequestorIdWithoutRequests() {
        Pageable pageable = Pageable.ofSize(10);
        user1 = createUser(1, "User1Name", "user1@mail.ru");

        List<ItemRequest> requests = itemRequestRepository.findOtherRequestsByRequestorId(user1.getId(), pageable);
        assertTrue(requests.isEmpty());
    }

    @Test
    public void findAllByRequestorIdSuccess() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");

        user1 = createUser(1, "User1Name", "user1@mail.ru");
        request1 = createRequest(1, "Request1Description", user1);
        request2 = createRequest(2, "Request2Description", user1);
        request3 = createRequest(3, "Request3Description", user1);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user1.getId(), sort);
        assertEquals(3, requests.size());
        assertEquals(request1, requests.get(0));
        assertEquals(request2, requests.get(1));
        assertEquals(request3, requests.get(2));

        List<ItemRequest> emptyRequests = itemRequestRepository.findAllByRequestorId(10, sort);
        assertTrue(emptyRequests.isEmpty());
    }

    @Test
    public void findAllByRequestorIdWithoutRequests() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        user1 = createUser(1, "User1Name", "user1@mail.ru");

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(user1.getId(), sort);
        assertTrue(requests.isEmpty());
    }

    private User createUser(Integer id, String name, String email) {
        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        return userRepository.save(user);
    }

    private ItemRequest createRequest(Integer id, String description, User user) {
        ItemRequest request = ItemRequest.builder()
                .id(id)
                .description(description)
                .createdDate(LocalDateTime.now())
                .requestor(user)
                .build();

        return itemRequestRepository.save(request);
    }
}
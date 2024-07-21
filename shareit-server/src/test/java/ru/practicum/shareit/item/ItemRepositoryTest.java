package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private Item item1;
    private Item item2;
    private ItemRequest request1;

    private static Pageable pageable;

    @BeforeAll
    public static void beforeAll() {
        pageable = Pageable.ofSize(10);
    }

    @Test
    public void findByTextWithoutItems() {
        List<Item> items = itemRepository.findByText("pwsl", Pageable.ofSize(1));
        assertTrue(items.isEmpty());
    }

    @Test
    public void findByTextInNameSuccess() {
        user1 = createUser(1, "Name", "name@mail.ru");
        request1 = createRequest(1, "Description", user1);
        item1 = createItem(1, "Name", "Description", true, user1, request1);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, request1);

        List<Item> items = itemRepository.findByText("nAm", pageable);
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));

        List<Item> newItems = itemRepository.findByText("ewNa", pageable);
        assertEquals(1, newItems.size());
        assertEquals(item2, newItems.get(0));

        List<Item> emptyList = itemRepository.findByText("baba", pageable);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void findByTextInDescriptionSuccess() {
        user1 = createUser(1, "Name", "name@mail.ru");
        request1 = createRequest(1, "Description", user1);
        item1 = createItem(1, "Name", "Description", true, user1, request1);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, request1);

        List<Item> items = itemRepository.findByText("dEscr", pageable);
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));

        List<Item> newItems = itemRepository.findByText("ewdESCr", pageable);
        assertEquals(1, newItems.size());
        assertEquals(item2, newItems.get(0));

        List<Item> emptyList = itemRepository.findByText("baba", pageable);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void findAllByOwnerIdWithoutItems() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Item> items = itemRepository.findAllByOwnerId(user1.getId(), pageable);
        assertTrue(items.isEmpty());
    }

    @Test
    void findAllByOwnerIdSuccess() {
        user1 = createUser(1, "Name", "name@mail.ru");
        request1 = createRequest(1, "Description", user1);
        item1 = createItem(1, "Name", "Description", true, user1, request1);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, request1);

        List<Item> items = itemRepository.findAllByOwnerId(user1.getId(), pageable);
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));

        List<Item> emptyItems = itemRepository.findAllByOwnerId(6, pageable);
        assertTrue(emptyItems.isEmpty());
    }

    @Test
    public void findAllByRequestIdWithoutItems() {
        user1 = createUser(1, "Name", "name@mail.ru");
        request1 = createRequest(1, "Description", user1);

        List<Item> items = itemRepository.findAllByRequestId(request1.getId());
        assertTrue(items.isEmpty());
    }


    @Test
    void findAllByRequestId() {
        user1 = createUser(1, "Name", "name@mail.ru");
        request1 = createRequest(1, "Description", user1);
        item1 = createItem(1, "Name", "Description", true, user1, request1);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, request1);

        List<Item> items = itemRepository.findAllByRequestId(request1.getId());
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));

        List<Item> emptyItems = itemRepository.findAllByRequestId(10);
        assertTrue(emptyItems.isEmpty());
    }

    private Item createItem(Integer id, String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .request(request)
                .owner(owner)
                .build();

        return itemRepository.save(item);
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
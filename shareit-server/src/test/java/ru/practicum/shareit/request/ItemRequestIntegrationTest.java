package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ItemRequestIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private ItemDto item;
    private ItemDto item2;
    private ItemRequestDto request;
    private ItemRequestDto request2;
    private ItemRequestDto request3;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        createRequests();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllItemRequestsByOwnerSuccess() {
        List<ItemRequestForOwnerDto> requests = itemRequestService.getAllItemRequestsByOwner(user.getId());

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals(requests.get(0).getId(), request.getId());
        assertEquals(requests.get(0).getDescription(), request.getDescription());
        assertEquals(requests.get(0).getItems(), List.of(item));
        assertEquals(requests.get(1).getId(), request2.getId());
        assertEquals(requests.get(1).getDescription(), request2.getDescription());
        assertEquals(requests.get(1).getItems(), Collections.emptyList());
    }

    @Test
    void getRequestByIdSuccess() {
        ItemRequestForOwnerDto result = itemRequestService.getRequestById(2, 1);

        assertNotNull(result);
        assertEquals(result.getId(), request2.getId());
        assertEquals(result.getDescription(), request2.getDescription());
        assertEquals(result.getItems(), Collections.emptyList());
    }

    @Test
    void getRequestPageableSuccess() {
        List<ItemRequestForOwnerDto> result = itemRequestService.getAllRequestsPageable(2, 0, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), request.getId());
        assertEquals(result.get(0).getDescription(), request.getDescription());
        assertEquals(result.get(0).getItems(), List.of(item));
    }


    private void createRequests() {
        user = User.builder()
                .id(1)
                .name("User")
                .email("mail@mail.ru")
                .build();

        userService.addUser(user);

        user2 = User.builder()
                .id(2)
                .name("User2")
                .email("mail2@mail.ru")
                .build();

        userService.addUser(user2);

        request = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .build();

        itemRequestService.addItemRequest(request, user.getId());

        request2 = ItemRequestDto.builder()
                .id(2)
                .description("2description")
                .build();

        itemRequestService.addItemRequest(request2, user.getId());

        request3 = ItemRequestDto.builder()
                .id(3)
                .description("3description")
                .build();

        itemRequestService.addItemRequest(request3, user2.getId());

        item = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("description")
                .available(true)
                .requestId(request.getId())
                .ownerId(user.getId())
                .build();

        itemService.addItem(item, user.getId());

        item2 = ItemDto.builder()
                .id(2)
                .name("item2Name")
                .description("2description")
                .available(true)
                .requestId(request3.getId())
                .ownerId(user2.getId())
                .build();

        itemService.addItem(item2, user2.getId());

    }
}

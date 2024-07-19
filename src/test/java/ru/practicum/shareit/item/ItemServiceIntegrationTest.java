package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ItemServiceIntegrationTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    private User user;
    private ItemDto item;
    private ItemDto item2;

    @BeforeEach
    void setUp() {
        createItemWithUser();
    }

    @Test
    void getAllItemByUserIdSuccess() {
        List<ItemForOwnerDto> items = itemService.getAllItemsOfUser(user.getId(), 0, 10);

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getAvailable(), item.getAvailable());
        assertEquals(items.get(1).getId(), item2.getId());
        assertEquals(items.get(1).getName(), item2.getName());
        assertEquals(items.get(1).getAvailable(), item2.getAvailable());
        assertEquals(items.get(0).getOwnerId(), user.getId());
        assertEquals(items.get(1).getOwnerId(), user.getId());
    }

    private void createItemWithUser() {
        user = User.builder()
                .id(1)
                .name("User")
                .email("mail@mail.ru")
                .build();

        userService.addUser(user);

        item = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("description")
                .available(true)
                .build();

        itemService.addItem(item, user.getId());

        item2 = ItemDto.builder()
                .id(2)
                .name("item2Name")
                .description("2description")
                .available(false)
                .build();

        itemService.addItem(item2, user.getId());
    }
}


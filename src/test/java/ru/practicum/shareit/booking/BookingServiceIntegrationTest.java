package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookingServiceIntegrationTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    private User user;
    private User user2;
    private ItemDto item;
    private ItemDto item2;
    private BookingDto booking;
    private BookingDto booking2;

    @BeforeEach
    void setUp() {
        createBooking();
    }

    @Test
    void getBookingByBookerOrItemOwnerSuccess() {
        BookingDto result = bookingService.getBookingByItemOwnerOrBooker(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getItemId(), booking.getItemId());
    }

    @Test
    void getAllBookingsByBookerId() {
        List<BookingDto> result = bookingService.getAllByBookerId(1, "ALL", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), booking2.getId());
    }

    @Test
    void getAllBookingsByItemOwner() {
        List<BookingDto> result = bookingService.getAllByOwnerId(1, "ALL", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), booking.getId());
    }

    private void createBooking() {
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
                .available(true)
                .build();

        itemService.addItem(item2, user2.getId());

        booking = BookingDto.builder()
                .id(1)
                .itemId(item.getId())
                .startDate(LocalDateTime.now().plusMinutes(30).toString())
                .endDate(LocalDateTime.now().plusHours(1).toString())
                .build();

        bookingService.createBooking(booking, user2.getId());

        booking2 = BookingDto.builder()
                .id(2)
                .itemId(item2.getId())
                .startDate(LocalDateTime.now().plusMinutes(30).toString())
                .endDate(LocalDateTime.now().plusHours(1).toString())
                .build();

        bookingService.createBooking(booking2, user.getId());
    }
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    private static Pageable pageable;

    @BeforeAll
    public static void beforeAll() {
        pageable = Pageable.ofSize(10);
    }

    @Test
    void findAllByBookerIdCurrent() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByBookerIdCurrent(user1.getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByBookerIdCurrent(10, LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    public void findAllByBookerIdCurrentWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Booking> bookings = bookingRepository.findAllByBookerIdCurrent(user1.getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndEndDateIsBefore() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndDateIsBefore(user1.getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking2, bookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByBookerIdAndEndDateIsBefore(10, LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndEndDateIsBeforeWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndDateIsBefore(user1.getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStartDateIsAfter() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartDateIsAfter(user1.getId(), LocalDateTime.now().plusMinutes(20), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByBookerIdAndStartDateIsAfter(20, LocalDateTime.now().plusMinutes(30), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStartDateIsAfterWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartDateIsAfter(user1.getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.APPROVED);

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(user1.getId(), Status.APPROVED, pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking2, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByBookerIdAndStatus(user1.getId(), Status.WAITING, pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking1, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByBookerIdAndStatus(user1.getId(), Status.REJECTED, pageable);
        assertTrue(emptyBookings.isEmpty());

        List<Booking> emptyBookings2 = bookingRepository.findAllByBookerIdAndStatus(20, Status.APPROVED, pageable);
        assertTrue(emptyBookings2.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStatusWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(user1.getId(), Status.APPROVED, pageable);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerId() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.APPROVED);

        List<Booking> bookings = bookingRepository.findAllByBookerId(user1.getId(), pageable);
        assertEquals(2, bookings.size());
        assertEquals(booking1, bookings.get(0));
        assertEquals(booking2, bookings.get(1));

        List<Booking> emptyBookings = bookingRepository.findAllByBookerId(10, pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        List<Booking> emptyBookings = bookingRepository.findAllByBookerId(user1.getId(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwner() {
        user1 = createUser(1, "Name", "name@mail.ru");
        user2 = createUser(2, "Name2", "name2@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user2, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.APPROVED);

        List<Booking> bookings = bookingRepository.findAllByItemOwner(item1.getOwner().getId(), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByItemOwner(item2.getOwner().getId(), pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking2, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwner(20, pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        List<Booking> emptyBookings = bookingRepository.findAllByItemOwner(item1.getOwner().getId(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllCurrentBookingByOwnerId() {
        user1 = createUser(1, "Name", "name@mail.ru");
        user2 = createUser(2, "Name2", "name2@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user2, null);
        booking1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                item2, user2, Status.WAITING);
        booking3 = createBooking(3, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllCurrentBookingByOwnerId(item1.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllCurrentBookingByOwnerId(item2.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking2, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllCurrentBookingByOwnerId(10, LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllCurrentBookingByOwnerIdWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);

        List<Booking> emptyBookings = bookingRepository.findAllCurrentBookingByOwnerId(item1.getOwner().getId(), LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndEndDateIsBefore() {
        user1 = createUser(1, "Name", "name@mail.ru");
        user2 = createUser(2, "Name2", "name2@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user2, null);
        booking1 = createBooking(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                item2, user2, Status.WAITING);
        booking3 = createBooking(3, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(item1.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(item2.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking3, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(10, LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndEndDateIsBeforeWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(item1.getOwner().getId(), LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStartDateIsAfter() {
        user1 = createUser(1, "Name", "name@mail.ru");
        user2 = createUser(2, "Name2", "name2@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user2, null);
        booking1 = createBooking(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                item2, user2, Status.WAITING);
        booking3 = createBooking(3, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(item1.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(item2.getOwner().getId(), LocalDateTime.now().plusMinutes(30), pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking2, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(10, LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStartDateIsAfterWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(item1.getOwner().getId(), LocalDateTime.now(), pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        user1 = createUser(1, "Name", "name@mail.ru");
        user2 = createUser(2, "Name2", "name2@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user2, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user2, Status.APPROVED);
        booking3 = createBooking(3, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.WAITING);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(item1.getOwner().getId(), Status.WAITING, pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByItemOwnerIdAndStatus(item2.getOwner().getId(), Status.WAITING, pageable);
        assertEquals(1, newBookings.size());
        assertEquals(booking3, newBookings.get(0));

        List<Booking> newBookings2 = bookingRepository.findAllByItemOwnerIdAndStatus(item2.getOwner().getId(), Status.APPROVED, pageable);
        assertEquals(1, newBookings2.size());
        assertEquals(booking2, newBookings2.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndStatus(item2.getOwner().getId(), Status.REJECTED, pageable);
        assertTrue(emptyBookings.isEmpty());

        List<Booking> emptyBookings2 = bookingRepository.findAllByItemOwnerIdAndStatus(20, Status.APPROVED, pageable);
        assertTrue(emptyBookings2.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStatusWithoutBookings() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);

        List<Booking> emptyBookings = bookingRepository.findAllByItemOwnerIdAndStatus(item1.getOwner().getId(), Status.APPROVED, pageable);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemId() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        booking1 = createBooking(1, LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(1),
                item1, user1, Status.WAITING);
        booking2 = createBooking(2, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5),
                item2, user1, Status.APPROVED);

        List<Booking> bookings = bookingRepository.findAllByItemId(item1.getId(), sort);
        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

        List<Booking> newBookings = bookingRepository.findAllByItemId(item2.getId(), sort);
        assertEquals(1, newBookings.size());
        assertEquals(booking2, newBookings.get(0));

        List<Booking> emptyBookings = bookingRepository.findAllByItemId(10, sort);
        assertTrue(emptyBookings.isEmpty());
    }

    @Test
    void findAllByItemIdWithoutBookings() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        List<Booking> emptyBookings = bookingRepository.findAllByItemId(item1.getId(), sort);
        assertTrue(emptyBookings.isEmpty());
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

    private Booking createBooking(Integer id, LocalDateTime startDate, LocalDateTime endDate, Item item, User booker, Status status) {
        Booking booking = Booking.builder()
                .id(id)
                .item(item)
                .startDate(startDate)
                .endDate(endDate)
                .booker(booker)
                .status(status)
                .build();

        return bookingRepository.save(booking);
    }
}
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;
    private List<Item> items;

    @BeforeEach
    public void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);
        user = User.builder()
                .id(1)
                .name("Name")
                .email("name@mail.ru")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .build();
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        item = Item.builder()
                .id(1)
                .owner(user)
                .available(true)
                .request(itemRequest)
                .description("description")
                .build();
        items = List.of(item);
    }

    @Test
    void addItemRequestThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addItemRequest(itemRequestDto, 1));
    }

    @Test
    void addItemRequestSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto request = itemRequestService.addItemRequest(itemRequestDto, 1);

        assertNotNull(request);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getAllItemRequestsByOwnerThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllItemRequestsByOwner(1));
    }

    @Test
    void getAllItemRequestsByOwnerSuccess() {
        itemRequest.setId(1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(anyInt(), any(Sort.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(items);

        List<ItemRequestForOwnerDto> requests = itemRequestService.getAllItemRequestsByOwner(anyInt());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()), requests.get(0).getItems());
    }

    @Test
    void getRequestById() {
    }

    @Test
    void getAllRequestsPageable() {
    }
}
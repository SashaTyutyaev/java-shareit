package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    private static final Sort SORT = Sort.by(Sort.Direction.ASC, "createdDate");

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = getUserById(userId);
        itemRequestDto.setRequestorId(user.getId());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestForOwnerDto> getAllItemRequestsByOwner(Integer userId) {
        getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId, SORT);

        List<ItemRequestForOwnerDto> requestForOwnerDtoList = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            ItemRequestForOwnerDto itemRequestForOwnerDto = ItemRequestMapper.toItemRequestForOwnerDto(itemRequest);
            List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());

            itemRequestForOwnerDto.setItems(items);

            requestForOwnerDtoList.add(itemRequestForOwnerDto);
        }

        return requestForOwnerDtoList;
    }

    @Override
    public ItemRequestForOwnerDto getRequestById(Integer requestId, Integer userId) {
        getUserById(userId);
        List<ItemDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequest itemRequest = getItemRequestById(requestId);
        ItemRequestForOwnerDto itemRequestForOwnerDto = ItemRequestMapper.toItemRequestForOwnerDto(itemRequest);
        itemRequestForOwnerDto.setItems(items);
        return itemRequestForOwnerDto;
    }

    @Override
    public List<ItemRequestForOwnerDto> getAllRequestsPageable(Integer userId, Integer from, Integer size) {
        getUserById(userId);

        if (from == null && size == null) {
            return Collections.emptyList();
        }

        if (from >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(from, size);

            List<ItemRequest> itemRequestPage = itemRequestRepository.findOtherRequestsByRequestorId(userId, pageable).getContent();

            List<ItemRequestForOwnerDto> itemRequestForOwnerDtoList =
                    itemRequestPage.stream()
                            .map(ItemRequestMapper::toItemRequestForOwnerDto)
                            .collect(Collectors.toList());

            for (ItemRequestForOwnerDto request : itemRequestForOwnerDtoList) {
                List<ItemDto> items = itemRepository.findAllByRequestId(request.getId())
                        .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
                request.setItems(items);
            }

            return itemRequestForOwnerDtoList;
        } else {
            log.error("Params from and size must be higher than 0");
            throw new IncorrectParameterException("Params from and size must be higher than 0");
        }
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }

    private ItemRequest getItemRequestById(Integer requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.error("The request with id {} is not found", requestId);
            return new EntityNotFoundException("The request with id " + requestId + " is not found");
        });
    }

    private Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("The item with id {} is not found", itemId);
            return new EntityNotFoundException("The item with id " + itemId + " is not found");
        });
    }
}

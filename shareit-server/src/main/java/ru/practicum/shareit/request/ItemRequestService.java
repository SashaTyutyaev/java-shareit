package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId);

    List<ItemRequestForOwnerDto> getAllItemRequestsByOwner(Integer userid);

    ItemRequestForOwnerDto getRequestById(Integer requestId, Integer userId);

    List<ItemRequestForOwnerDto> getAllRequestsPageable(Integer userId, Integer from, Integer size);
}

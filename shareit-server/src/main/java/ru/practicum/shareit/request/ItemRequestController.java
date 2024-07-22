package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestForOwnerDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllItemRequestsByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestForOwnerDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @PathVariable Integer requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestForOwnerDto> getPageableRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(required = false) Integer from,
                                                            @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllRequestsPageable(userId, from, size);
    }
}

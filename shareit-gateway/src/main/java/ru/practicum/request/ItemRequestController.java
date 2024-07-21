package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.add(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getRequestsByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @PathVariable Integer requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPageableRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestClient.getPageableRequests(userId, from, size);
    }
}

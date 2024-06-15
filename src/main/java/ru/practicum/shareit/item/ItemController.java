package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam String text) {
        return itemService.searchItems(text);
    }
}

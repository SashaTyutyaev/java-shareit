package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto item, Integer userId);

    ItemDto updateItem(ItemDto item, Integer itemId, Integer userId);

    List<ItemDto> getAllItemsOfUser(Integer userId);

    ItemDto getItemById(Integer itemId);

    List<ItemDto> searchItems(String text);
}

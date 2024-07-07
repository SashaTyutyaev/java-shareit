package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto item, Integer userId);

    ItemDto updateItem(ItemDto item, Integer itemId, Integer userId);

    List<ItemForOwnerDto> getAllItemsOfUser(Integer userId);

    ItemForOwnerDto getItemDtoById(Integer itemId, Integer userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId);
}

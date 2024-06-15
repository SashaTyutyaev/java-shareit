package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository {

    Item addItem(Item item, Integer userId);

    Item updateItem(Item item, Integer itemId, Integer userId);

    List<Item> getAllItemsOfUser(Integer userId);

    Optional<Item> getItemById(Integer itemId);

    List<Item> searchItems(String text);

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(ItemDto item, Integer userId) {
        return itemRepository.addItem(item, userId);
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        return itemRepository.updateItem(item, itemId, userId);
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
        return itemRepository.getAllItemsOfUser(userId);
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItems(text);
        }
    }
}

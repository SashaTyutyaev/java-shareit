package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
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
    public ItemDto getItemById(Integer itemId, Integer userId) {
        return itemRepository.getItemByIdOfUser(itemId, userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text);
    }
}

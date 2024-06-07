package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final List<ItemDto> items = new ArrayList<>();
    private final Map<Integer, List<ItemDto>> usersItems = new HashMap<>();

    private int generatedId = 0;

    private int generateId() {
        return generatedId++;
    }

    @Override
    public ItemDto addItem(ItemDto item, Integer userId) {
        item.setId(generateId());
        usersItems.compute(item.getOwnerId(), (userId2, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        items.add(item);
        log.info("Add item {} with owner ID - {} is success", item.getId(), userId);
        return item;
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
            ItemDto itemDto = items.get(itemId);

            if (item.getName() != null && !item.getName().isBlank()) {
                itemDto.setName(item.getName());
            }

            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                itemDto.setDescription(item.getDescription());
            }

            if (item.getIsAvailable() != null) {
                itemDto.setIsAvailable(item.getIsAvailable());
            }

            return itemDto;

    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
            log.info("Get all items of user {}", userId);
            return usersItems.get(userId);
    }

    @Override
    public ItemDto getItemByIdOfUser(Integer itemId, Integer userId) {
        List<ItemDto> itemDtoList = usersItems.get(userId);
        log.info("Get item {} of user {} is success", itemId, userId);
        return itemDtoList.get(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (ItemDto itemDto : items) {
            if ((itemDto.getName().toUpperCase().contains(text.toUpperCase()) ||
                    itemDto.getDescription().toUpperCase().contains(text.toUpperCase())) && itemDto.getIsAvailable()) {
                itemDtoList.add(itemDto);
            }
        }
        log.info("Search items success");
        return itemDtoList;
    }

}

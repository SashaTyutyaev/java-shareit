package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private int generatedId = 1;

    private int generateId() {
        return generatedId++;
    }

    @Override
    public Item addItem(Item item, Integer userId) {
        item.setId(generateId());
        items.put(item.getId(), item);
        log.info("Add item {} with owner ID - {} is success", item.getId(), userId);
        return item;
    }

    @Override
    public Item updateItem(Item item, Integer itemId, Integer userId) {
        Item item2 = items.get(itemId);

        if (item.getName() != null && !item.getName().isBlank()) {
            item2.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            item2.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            item2.setAvailable(item.getAvailable());
        }
        return item2;
    }

    @Override
    public List<Item> getAllItemsOfUser(Integer userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase()) && item.getAvailable() ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase()) && item.getAvailable())
                .collect(Collectors.toList());
    }
}

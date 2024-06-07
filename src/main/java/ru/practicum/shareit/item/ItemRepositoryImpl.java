package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private final Map<Integer, ItemDto> items = new HashMap<>();

    private int generatedId = 1;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private int generateId() {
        return generatedId++;
    }

    @Override
    public ItemDto addItem(ItemDto item, Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found");
        } else {
            item.setId(generateId());
            item.setOwnerId(userId);
            items.put(item.getId(), item);
            log.info("Add item {} with owner ID - {} is success", item.getId(), userId);
            return item;
        }
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        if (!Objects.equals(items.get(itemId).getOwnerId(), userId)) {
            log.error("The item with id {} is not owned by user {}", itemId, userId);
            throw new EntityNotFoundException("The item with id " + itemId + " is not owned by user " + userId);
        }
        ItemDto itemDto = items.get(itemId);

        if (item.getName() != null && !item.getName().isBlank()) {
            itemDto.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemDto.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemDto.setAvailable(item.getAvailable());
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (ItemDto itemDto : items.values()) {
            if ((itemDto.getName().toUpperCase().contains(text.toUpperCase()) ||
                    itemDto.getDescription().toUpperCase().contains(text.toUpperCase())) && itemDto.getAvailable()) {
                itemDtoList.add(itemDto);
            }
        }
        log.info("Search items success");
        return itemDtoList;
    }

}

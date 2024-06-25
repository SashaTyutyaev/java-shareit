package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional()
    public ItemDto addItem(ItemDto item, Integer userId) {
        User user = getUserById(userId);
        Item itemToSave = ItemMapper.toItemFromDto(item);
        itemToSave.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(itemToSave));
    }

    @Override
    @Transactional()
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        User user = getUserById(userId);
        Item optItem = getItemById(itemId);
        if (!optItem.getOwner().equals(user)) {
            log.error("The item with id {} is not owned by user {}", itemId, userId);
            throw new EntityNotFoundException("The item with id " + itemId + " is not owned by user " + userId);
        }

        if (item.getAvailable() != null) {
            optItem.setAvailable(item.getAvailable());
        }

        if (item.getName() != null) {
            optItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            optItem.setDescription(item.getDescription());
        }
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(optItem));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
        getUserById(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(Integer itemId) {
        Item item = getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text,text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    private Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("The item with id {} is not found", itemId);
            return new EntityNotFoundException("The item with id " + itemId + " is not found");
        });
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }
}

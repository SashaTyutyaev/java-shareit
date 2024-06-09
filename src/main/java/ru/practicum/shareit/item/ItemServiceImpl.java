package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

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
    public ItemDto addItem(ItemDto item, Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found");
        } else {
            Item itemToSave = ItemMapper.toItemFromDto(item);
            itemToSave.setOwner(userRepository.getUserById(userId));
            return ItemMapper.toItemDto(itemRepository.addItem(itemToSave, userId));
        }
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        if (userRepository.getUserById(userId) == null) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        if (!itemRepository.getItemById(itemId).getOwner().equals(userRepository.getUserById(userId))) {
            log.error("The item with id {} is not owned by user {}", itemId, userId);
            throw new EntityNotFoundException("The item with id " + itemId + " is not owned by user " + userId);
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItemFromDto(item), itemId, userId));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
        return itemRepository.getAllItemsOfUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItems(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}

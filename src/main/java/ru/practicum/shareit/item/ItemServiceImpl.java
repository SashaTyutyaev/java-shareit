package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public ItemDto addItem(ItemDto item, Integer userId) {
        User user = userRepository.getUserById(userId).orElseThrow();
        Item itemToSave = ItemMapper.toItemFromDto(item);
        itemToSave.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.addItem(itemToSave, userId));
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        User user = userRepository.getUserById(userId).orElseThrow();
        if (!itemRepository.getItemById(itemId).getOwner().equals(user)) {
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

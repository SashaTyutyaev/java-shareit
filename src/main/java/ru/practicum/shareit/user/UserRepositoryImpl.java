package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    private final Set<String> userEmails = new HashSet<>();

    private int generatedId = 1;

    private int generateId() {
        return generatedId++;
    }

    @Override
    public UserDto addUser(User user) {
        if (!userEmails.contains(user.getEmail())) {
            user.setId(generateId());
            users.put(user.getId(), user);
            userEmails.add(user.getEmail());
            log.info("Add user with ID - {}", user.getId());
            return UserMapper.toUserDto(user);
        } else {
            log.error("Email already in use");
            throw new EntityAlreadyExistsException("Email is already in use");
        }
    }

    @Override
    public UserDto updateUser(User user, Integer userId) {
        if (users.containsKey(userId)) {
            if (userEmails.contains(user.getEmail()) && user.getEmail() != null) {
                throw new EntityAlreadyExistsException("Email is already in use");
            }

            if (user.getEmail() != null) {
                userEmails.remove(users.get(userId).getEmail());
                users.get(userId).setEmail(user.getEmail());
            }

            if (user.getName() != null) {
                users.get(userId).setName(user.getName());
            }

            User userDto =  users.get(userId);
            return UserMapper.toUserDto(userDto);
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> usersList = new ArrayList<>();
        for (User user : users.values()) {
            usersList.add(UserMapper.toUserDto(user));
        }
        log.info("Get all users success");
        return usersList;
    }

    @Override
    public UserDto getUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User is not found");
        } else {
            User user = users.get(userId);
            log.info("Get user with ID - {} success", userId);
            return UserMapper.toUserDto(user);
        }
    }

    @Override
    public void deleteUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User is not found");
        } else {
            userEmails.remove(users.get(userId).getEmail());
            users.remove(userId);
            log.info("Delete user with ID - {} success", userId);
        }
    }
}

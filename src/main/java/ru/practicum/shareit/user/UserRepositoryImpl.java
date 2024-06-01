package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    private int generatedId = 1;

    private int generatedId() {
        return generatedId++;
    }

    @Override
    public User addUser(User user) {
        user.setId(generatedId());
        users.put(user.getId(), user);
        log.info("Add user with ID - {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with ID {} not found", user.getId());
            throw new EntityNotFoundException("User is not found");
        } else {
            users.put(user.getId(), user);
            log.info("Update user with ID - {}", user.getId());
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Get all users success");
        return usersList;
    }

    @Override
    public User getUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User is not found");
        } else {
            User user = users.get(userId);
            log.info("Get user with ID - {} success", userId);
            return user;
        }
    }

    @Override
    public void deleteUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("User with ID {} not found", userId);
            throw new EntityNotFoundException("User is not found");
        } else {
            users.remove(userId);
            log.info("Delete user with ID - {} success", userId);
        }
    }
}

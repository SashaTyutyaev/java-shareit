package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    private int generatedId = 1;

    private int generateId() {
        return generatedId++;
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Add user with ID - {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user, Integer userId) {
        if (user.getEmail() != null) {
            users.get(userId).setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }

        log.info("Update user with ID - {}", userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Get all users success");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        User user = users.get(userId);
        log.info("Get user with ID - {} success", userId);
        return Optional.ofNullable(user);
    }

    @Override
    public void deleteUserById(Integer userId) {
        users.remove(userId);
        log.info("Delete user with ID - {} success", userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }
}

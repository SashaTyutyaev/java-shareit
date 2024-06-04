package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;

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
    public User addUser(User user) {
        if (!userEmails.contains(user.getEmail())) {
            user.setId(generateId());
            users.put(user.getId(), user);
            userEmails.add(user.getEmail());
            log.info("Add user with ID - {}", user.getId());
            return user;
        } else {
            log.error("Email already in use");
            throw new EntityAlreadyExistsException("Email is already in use");
        }
    }

    @Override
    public User updateUser(User user, Integer userId) {
        if (users.containsKey(userId)) {
            if (userEmails.contains(user.getEmail()) && user.getEmail() != null) {
                throw new EntityAlreadyExistsException("Email is already in use");
            }

            if (user.getEmail() != null) {
                userEmails.remove(user.getEmail());
            }

            if (user.getEmail() != null) {
                users.get(userId).setEmail(user.getEmail());
            }

            if (user.getName() != null) {
                users.get(userId).setName(user.getName());
            }

            return users.get(userId);
        } else {
            throw new EntityNotFoundException("User not found");
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

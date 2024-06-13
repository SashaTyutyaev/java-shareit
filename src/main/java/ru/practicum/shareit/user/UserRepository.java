package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User addUser(User user);

    User updateUser(User user, Integer userId);

    List<User> getAllUsers();

    Optional<User> getUserById(Integer userId);

    void deleteUserById(Integer userId);
}

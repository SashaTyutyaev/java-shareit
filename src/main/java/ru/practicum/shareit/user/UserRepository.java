package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User addUser(User user);
    User updateUser(User user, Integer userId);
    List<User> getAllUsers();
    User getUserById(Integer userId);
    void deleteUserById(Integer userId);
}

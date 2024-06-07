package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    UserDto addUser(User user);

    UserDto updateUser(User user, Integer userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(Integer userId);

    void deleteUserById(Integer userId);
}

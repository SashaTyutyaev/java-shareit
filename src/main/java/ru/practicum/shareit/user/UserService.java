package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(@Valid User user) {
        return userRepository.addUser(user);
    }

    public UserDto updateUser(User user, Integer userId) {
        return userRepository.updateUser(user, userId);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(Integer userId) {
        return userRepository.getUserById(userId);
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteUserById(userId);
    }
}

package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(@Valid User user) {
        User userDto = userRepository.addUser(user);
        return UserMapper.toUserDto(userDto);
    }

    public UserDto updateUser(User user, Integer userId) {
        User userDto = userRepository.updateUser(user, userId);
        return UserMapper.toUserDto(userDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Integer userId) {
        User userDto = userRepository.getUserById(userId).orElseThrow();
        return UserMapper.toUserDto(userDto);
    }

    public void deleteUserById(Integer userId) {
        User userDto = userRepository.getUserById(userId).orElseThrow();
        userRepository.deleteUserById(userId);
    }
}

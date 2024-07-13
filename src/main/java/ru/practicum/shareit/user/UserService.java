package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        userRepository.save(UserMapper.toUser(userDto));
        return userDto;
    }

    public UserDto updateUser(User user, Integer userId) {
        User user2 = getUserById(userId);

        if (user.getEmail() != null) {
            user2.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            user2.setName(user.getName());
        }

        User userDto = userRepository.saveAndFlush(user2);
        return UserMapper.toUserDto(userDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserDtoById(Integer userId) {
        User userDto = getUserById(userId);
        return UserMapper.toUserDto(userDto);
    }

    public void deleteUserById(Integer userId) {
        User userDto = getUserById(userId);
        userRepository.delete(userDto);
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }
}

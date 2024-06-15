package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDto addUser(@Valid User user) {
        for (User userInList : userRepository.getAllUsers()) {
            if (userInList.getEmail().equals(user.getEmail())) {
                log.error("User with email already exists");
                throw new EntityAlreadyExistsException("User with email " + user.getEmail() + " already exists");
            }
        }
        User userDto = userRepository.addUser(user);
        return UserMapper.toUserDto(userDto);
    }

    public UserDto updateUser(User user, Integer userId) {
        for (User userInList : userRepository.getAllUsers()) {
            if (userInList.getEmail().equals(user.getEmail())) {
                User user2 = getOptionalUserById(userId);
                if (!user2.getEmail().equals(user.getEmail())) {
                    log.error("User with email already exists");
                    throw new EntityAlreadyExistsException("User with email " + user.getEmail() + " already exists");
                }
            }
        }

        User userDto = userRepository.updateUser(user, userId);
        return UserMapper.toUserDto(userDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Integer userId) {
        User userDto = getOptionalUserById(userId);
        return UserMapper.toUserDto(userDto);
    }

    public void deleteUserById(Integer userId) {
        User userDto = getOptionalUserById(userId);
        userRepository.deleteUserById(userId);
    }

    private User getOptionalUserById(Integer userId) {
        return userRepository.getUserById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }
}

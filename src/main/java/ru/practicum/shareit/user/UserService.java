package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;

    public User addUser(@Valid User user) {
            return userRepository.addUser(user);
    }

    public User updateUser(@Valid User user, Integer userId) {
        return userRepository.updateUser(user, userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(Integer userId) {
        return userRepository.getUserById(userId);
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteUserById(userId);
    }
}

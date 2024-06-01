package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PatchMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("{id}")
    public void deleteUserById(@PathVariable Integer id) {
        userService.deleteUserById(id);
    }
}

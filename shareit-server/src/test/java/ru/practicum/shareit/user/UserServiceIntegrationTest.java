package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    private User user;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        createUsers();
    }

    @Test
    void getAllUsersSuccess() {
        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals(users.get(0).getId(), user.getId());
        assertEquals(users.get(0).getName(), user.getName());
        assertEquals(users.get(0).getEmail(), user.getEmail());
        assertEquals(users.get(1).getId(), user2.getId());
        assertEquals(users.get(1).getName(), user2.getName());
        assertEquals(users.get(1).getEmail(), user2.getEmail());
        assertEquals(users.get(2).getId(), user3.getId());
        assertEquals(users.get(2).getName(), user3.getName());
        assertEquals(users.get(2).getEmail(), user3.getEmail());
    }

    @Test
    void updateUsersNameSuccess() {
        User newUser = User.builder()
                .name("newName")
                .build();

        UserDto result = userService.updateUser(newUser, 1);

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), newUser.getName());
        assertEquals(result.getEmail(), user.getEmail());
    }

    @Test
    void updateUsersAllFieldsSuccess() {
        User newUser = User.builder()
                .name("newName")
                .email("newMail@ya.ru")
                .build();

        UserDto result = userService.updateUser(newUser, 1);

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), newUser.getName());
        assertEquals(result.getEmail(), newUser.getEmail());
    }

    @Test
    void updateUserThrowsEntityNotFoundExceptionWhenUserNotFound() {
        User newUser = User.builder()
                .name("newName")
                .email("newMail@ya.ru")
                .build();

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(newUser, 100));
    }

    @Test
    void getUserByIdThrowsEntityNotFoundExceptionWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(200));
    }

    @Test
    void getUserByIdSuccess() {
        UserDto result = userService.getUserDtoById(1);

        assertNotNull(result);
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getName(), user.getName());
        assertEquals(result.getEmail(), user.getEmail());

        UserDto result2 = userService.getUserDtoById(2);

        assertNotNull(result2);
        assertEquals(result2.getId(), user2.getId());
        assertEquals(result2.getName(), user2.getName());
        assertEquals(result2.getEmail(), user2.getEmail());

        UserDto result3 = userService.getUserDtoById(3);

        assertNotNull(result3);
        assertEquals(result3.getId(), user3.getId());
        assertEquals(result3.getName(), user3.getName());
        assertEquals(result3.getEmail(), user3.getEmail());
    }

    @Test
    void deleteUserById() {
        List<UserDto> users = userService.getAllUsers();

        assertEquals(3, users.size());

        userService.deleteUserById(1);
        List<User> usersAfterDel = userService.getAllUsers()
                .stream().map(UserMapper::toUser).collect(Collectors.toList());

        assertEquals(2, usersAfterDel.size());
        assertEquals(usersAfterDel.get(0), user2);
        assertEquals(usersAfterDel.get(1), user3);
    }


    private void createUsers() {
        user = User.builder()
                .id(1)
                .name("User")
                .email("mail@mail.ru")
                .build();

        userService.addUser(user);

        user2 = User.builder()
                .id(2)
                .name("User2")
                .email("mail2@mail.ru")
                .build();

        userService.addUser(user2);

        user3 = User.builder()
                .id(3)
                .name("User3")
                .email("mail3@mail.ru")
                .build();

        userService.addUser(user3);

    }
}

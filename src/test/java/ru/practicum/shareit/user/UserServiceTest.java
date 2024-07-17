package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private User user;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
        user = User.builder()
                .id(1)
                .name("Name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void addUserSuccess() {
        when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = userService.addUser(userDto);

        assertNotNull(userDto1);
        assertEquals(userDto, userDto1);
    }

    @Test
    void updateUserThrowsEntityNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(user, 1));
    }

    @Test
    void updateUsersNameAndEmailSuccess() {
        User updatedUser = User.builder()
                .id(1)
                .name("updateName")
                .email("update@mail.ru")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updatedUser);

        UserDto resultUser = userService.updateUser(updatedUser, 1);

        assertNotNull(resultUser);
        assertEquals(resultUser.getId(), updatedUser.getId());
        assertEquals(resultUser.getName(), updatedUser.getName());
        assertEquals(resultUser.getEmail(), updatedUser.getEmail());

    }

    @Test
    void updateUsersNameSuccess() {
        User updatedUser = User.builder()
                .id(1)
                .name("updateName")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updatedUser);

        UserDto resultUser = userService.updateUser(updatedUser, 1);

        assertNotNull(resultUser);
        assertEquals(resultUser.getId(), updatedUser.getId());
        assertEquals(resultUser.getName(), updatedUser.getName());
        assertNull(resultUser.getEmail());
    }

    @Test
    void updateUsersEmailSuccess() {
        User updatedUser = User.builder()
                .id(1)
                .email("updated@mail.ru")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updatedUser);

        UserDto resultUser = userService.updateUser(updatedUser, 1);

        assertNotNull(resultUser);
        assertEquals(resultUser.getId(), updatedUser.getId());
        assertEquals(resultUser.getEmail(), updatedUser.getEmail());
        assertNull(resultUser.getName());
    }

    @Test
    void updateUserWithoutNewFields() {
        User updatedUser = User.builder()
                .id(1)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updatedUser);

        UserDto resultUser = userService.updateUser(updatedUser, 1);

        assertNotNull(resultUser);
        assertEquals(resultUser.getId(), updatedUser.getId());
        assertEquals(resultUser.getName(), updatedUser.getName());
        assertEquals(resultUser.getEmail(), updatedUser.getEmail());
        assertNull(resultUser.getEmail());
        assertNull(resultUser.getName());
    }

    @Test
    void getAllUsersSuccess() {
        User newUser = User.builder()
                .id(1)
                .name("updateName")
                .email("update@mail.ru")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user, newUser));

        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(user.getId(), users.get(0).getId());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user.getEmail(), users.get(0).getEmail());
        assertEquals(newUser.getId(), users.get(1).getId());
        assertEquals(newUser.getName(), users.get(1).getName());
        assertEquals(newUser.getEmail(), users.get(1).getEmail());
    }

    @Test
    void getAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void getUserDtoByIdSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto returnedUser = userService.getUserDtoById(1);

        assertNotNull(returnedUser);
        assertEquals(returnedUser.getId(), user.getId());
        assertEquals(returnedUser.getName(), user.getName());
        assertEquals(returnedUser.getEmail(), user.getEmail());
    }

    @Test
    void getUserDtoByIdThrowsEntityNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(anyInt()));
    }

    @Test
    void deleteUserByIdSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserById(1);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserByIdThrowsEntityNotFoundException() {
        when(userRepository.findById(100)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(100));

        verify(userRepository, times(1)).findById(100);
    }
}
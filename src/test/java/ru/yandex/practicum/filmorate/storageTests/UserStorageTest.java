package ru.yandex.practicum.filmorate.storageTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStorageTest {
    private final UserDbStorage userStorage;
    private final ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void testAddNewUser() throws JsonProcessingException {
        String json = "{\"login\":\"dolore\", " +
                "\"name\":\"Nick Name\", " +
                "\"email\":\"mail@mail.ru\", " +
                "\"birthday\":\"1946-08-20\"}";
        User user = objectMapper.readValue(json, User.class);
        assertThat(userStorage.addNewUser(user))
                .extracting(User::getLogin,
                        User::getName,
                        User::getEmail,
                        User::getBirthday)
                .containsExactly(user.getLogin(),
                        user.getName(),
                        user.getEmail(),
                        user.getBirthday());

        User user2 = userStorage.addNewUser(user.toBuilder()
                .login("marty")
                .email("pochta@mail.ru")
                .birthday(LocalDate.of(2000, 4, 11))
                .build());
        assertThat(user2)
                .extracting(User::getLogin,
                        User::getName,
                        User::getEmail,
                        User::getBirthday)
                .containsExactly(user2.getLogin(),
                        user2.getName(),
                        user2.getEmail(),
                        user2.getBirthday());
    }

    @Test
    @Order(2)
    public void testGetUserById() {
        Optional<User> user = userStorage.getUserById(1);
        assertThat(user)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1)
                );

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() ->
                        userStorage.getUserById(999))
                .withMessage("Пользователь с id 999 не найден");
    }

    @Test
    @Order(3)
    public void testGetUsers() {
        Collection<User> users = userStorage.getUsers();
        assertThat(users).asList().hasSize(2);
    }

    @Test
    @Order(4)
    public void testUpdateUser() {
        User userForUpdate = userStorage.getUserById(2).get()
                .toBuilder()
                .login("johny")
                .build();
        assertThat(userStorage.updateUser(userForUpdate))
                .extracting(User::getId,
                        User::getLogin,
                        User::getName,
                        User::getEmail,
                        User::getBirthday)
                .containsExactly(userForUpdate.getId(),
                        userForUpdate.getLogin(),
                        userForUpdate.getName(),
                        userForUpdate.getEmail(),
                        userForUpdate.getBirthday());
    }

    @Test
    @Order(5)
    public void testFriends() {
        User user = userStorage.getUserById(2).get();
        Set<User> userSet = new HashSet<>(Collections.singleton(user));

        userStorage.addFriend(1, 2);
        assertEquals(userStorage.getUserFriends(1), userSet);

        userStorage.addNewUser(user.toBuilder()
                .id(0)
                .login("lois")
                .email("pochta@pochta.com")
                .build());
        userStorage.addFriend(3, 2);
        assertEquals(userStorage.getCommonFriends(1, 3), userSet);

        userStorage.removeFriend(1, 2);
        assertEquals(userStorage.getUserFriends(1), new HashSet<>());
    }
}

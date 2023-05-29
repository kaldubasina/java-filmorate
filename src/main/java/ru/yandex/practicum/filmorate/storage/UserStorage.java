package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> getUsers();

    Optional<User> getUserById(Integer id);

    User addNewUser(User user);

    User updateUser(User user);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Set<User> getUserFriends(Integer userId);

    Set<User> getCommonFriends(Integer id, Integer otherId);
}

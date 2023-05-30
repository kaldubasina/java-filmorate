package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> getAll();

    Optional<User> getById(Integer id);

    User addNew(User user);

    User update(User user);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Set<User> getFriendsByUserId(Integer userId);

    Set<User> getCommonFriends(Integer id, Integer otherId);
}

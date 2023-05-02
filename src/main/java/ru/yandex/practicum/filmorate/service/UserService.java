package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addNewUser(User user) {
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());
        log.debug("Пользователи с id {} и {} стали друзьями", userId, friendId);
    }

    public void removeFromFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());
        log.debug("Пользователи с id {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        Collection<Integer> friendsId = userStorage.getUserById(userId).getFriendsId();
        Collection<User> friends = userStorage.getUsers();
        friends.removeIf(v -> !friendsId.contains(v.getId()));
        log.debug("Получение списка друзей пользователя с id {}", userId);
        return friends;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        Collection<User> userFriendsId = getFriends(userId);
        Collection<User> otherFriendsId = getFriends(otherId);
        userFriendsId.retainAll(otherFriendsId);
        log.debug("Получение списка общих друзей пользователей с id {} и {}", userId, otherId);
        return userFriendsId;
    }
}

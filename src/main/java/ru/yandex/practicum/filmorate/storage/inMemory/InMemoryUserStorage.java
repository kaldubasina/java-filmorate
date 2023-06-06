package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private static AtomicInteger id = new AtomicInteger(1);

    @Override
    public Collection<User> getAll() {
        log.debug("Получение списка пользователей ({} шт.)", userMap.values().size());
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> getById(Integer id) {
        if (isExist(id)) {
            log.debug("Попытка найти пользователя с несуществующим id {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return Optional.of(userMap.get(id));
    }

    @Override
    public User addNew(User user) {
        user.setId(id.getAndIncrement());
        log.debug("Добавлен новый пользователь: {}", user);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (isExist(user.getId())) {
            log.debug("Пользователя с id {} не существует", user.getId());
            throw new UserNotFoundException("Пользователя с id " + user.getId() + " не существует");
        }
        log.debug("Пользователь с id {} обновлен", user.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = getById(userId).get();
        User friend = getById(friendId).get();
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        log.debug("Пользователи с id {} и {} стали друзьями", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = getById(userId).get();
        User friend = getById(friendId).get();
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        log.debug("Пользователи с id {} и {} больше не друзья", userId, friendId);
    }

    @Override
    public Set<User> getFriendsByUserId(Integer userId) {
        log.debug("Получение списка друзей пользователя с id {}", userId);
        return getById(userId).get().getFriends();
    }

    @Override
    public Set<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<User> userFriends = getFriendsByUserId(userId);
        Set<User> otherFriends = getFriendsByUserId(otherId);
        userFriends.retainAll(otherFriends);
        log.debug("Получение списка общих друзей пользователей с id {} и {}", userId, otherId);
        return userFriends;
    }

    private boolean isExist(int id) {
        return !userMap.containsKey(id);
    }
}

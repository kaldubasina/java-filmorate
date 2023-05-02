package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private static AtomicInteger id = new AtomicInteger(1);

    @Override
    public Collection<User> getUsers() {
        log.debug("Получение списка пользователей ({} шт.)", userMap.values().size());
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (isExist(id)) {
            log.debug("Попытка найти пользователя с несуществующим id {}", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return userMap.get(id);
    }

    @Override
    public User addNewUser(User user) {
        user.setId(id.getAndIncrement());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Добавлен новый пользователь: {}", user);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (isExist(user.getId())) {
            log.debug("Пользователя с id {} не существует", user.getId());
            throw new UserNotFoundException("Пользователя с id " + user.getId() + " не существует");
        }
        log.debug("Пользователь с id {} обновлен", user.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    private boolean isExist(int id) {
        return !userMap.containsKey(id);
    }
}

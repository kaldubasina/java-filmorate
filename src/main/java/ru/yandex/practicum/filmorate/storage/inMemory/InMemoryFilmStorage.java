package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private static AtomicInteger id = new AtomicInteger(1);

    @Override
    public Collection<Film> getAll() {
        log.debug("Получение списка фильмов ({} шт.)", filmMap.values().size());
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Optional<Film> getById(Integer id) {
        if (isExist(id)) {
            log.debug("Попытка найти фильм с несуществующим id {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        return Optional.of(filmMap.get(id));
    }

    @Override
    public Film addNew(Film film) {
        film.setId(id.getAndIncrement());
        log.debug("Добавлен новый фильм: {}", film);
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isExist(film.getId())) {
            log.debug("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        log.debug("Фильм с id {} обновлен", film.getId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        getById(filmId).get().getLikedUsers().add(userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        if (!getById(filmId).get().getLikedUsers().removeIf(i -> i.equals(userId))) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        return getAll().stream()
                .sorted((f1, f2) -> f2.getRate() - f1.getRate())
                .limit(count)
                .collect(Collectors.toSet());
    }

    private boolean isExist(int id) {
        return !filmMap.containsKey(id);
    }
}

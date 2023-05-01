package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private static AtomicInteger id = new AtomicInteger(1);

    @Override
    public Collection<Film> getFilms() {
        log.debug("Получение списка фильмов ({} шт.)", filmMap.values().size());
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (isExist(id)) {
            log.debug("Попытка найти фильм с несуществующим id {}", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        return filmMap.get(id);
    }

    @Override
    public Film addNewFilm(Film film) {
        film.setId(id.getAndIncrement());
        log.debug("Добавлен новый фильм: {}", film);
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (isExist(film.getId())) {
            log.debug("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        log.debug("Фильм с id {} обновлен", film.getId());
        filmMap.put(film.getId(), film);
        return film;
    }

    private boolean isExist(int id) {
        return !filmMap.containsKey(id);
    }
}

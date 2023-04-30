package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film addNewFilm(Film film) {
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId).getLikedUsers().add(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!filmStorage.getFilmById(filmId).getLikedUsers().removeIf(i -> i.equals(userId))) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> f2.getLikedUsers().size() - f1.getLikedUsers().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilmById(Integer id);

    Film addNewFilm(Film film);

    Film updateFilm(Film film);
}

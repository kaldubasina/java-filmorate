package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getById(id);
    }

    public Collection<Genre> getGenres() {
        return genreDbStorage.getAll();
    }

    public Genre addNewGenre(Genre genre) {
        return genreDbStorage.addNew(genre);
    }

    public Genre updateGenre(Genre genre) {
        return genreDbStorage.update(genre);
    }

    public void removeGenreById(Integer id) {
        genreDbStorage.removeById(id);
    }
}

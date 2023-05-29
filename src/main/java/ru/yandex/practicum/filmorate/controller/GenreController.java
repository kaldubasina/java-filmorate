package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable Integer genreId) {
        return genreService.getGenreById(genreId);
    }

    @GetMapping
    public Collection<Genre> getGenres() {
        return genreService.getGenres();
    }

    @PostMapping
    public Genre addNewGenre(@Valid @RequestBody Genre genre) {
        return genreService.addNewGenre(genre);
    }

    @PutMapping
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        return genreService.updateGenre(genre);
    }

    @DeleteMapping("/{genreId}")
    public void removeGenreById(@PathVariable Integer genreId) {
        genreService.removeGenreById(genreId);
    }
}

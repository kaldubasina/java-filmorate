package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{mpaId}")
    public Mpa getGenreById(@PathVariable Integer mpaId) {
        return mpaService.getMpaById(mpaId);
    }

    @GetMapping
    public Collection<Mpa> getGenres() {
        return mpaService.getMpas();
    }

    @PostMapping
    public Mpa addNewGenre(@Valid @RequestBody Mpa mpa) {
        return mpaService.addNewMpa(mpa);
    }

    @PutMapping
    public Mpa updateGenre(@Valid @RequestBody Mpa mpa) {
        return mpaService.updateMpa(mpa);
    }

    @DeleteMapping("/{mpaId}")
    public void removeGenreById(@PathVariable Integer mpaId) {
        mpaService.removeMpaById(mpaId);
    }
}

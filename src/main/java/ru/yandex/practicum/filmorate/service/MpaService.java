package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(Integer id) {
        return mpaDbStorage.getMpaById(id);
    }

    public Collection<Mpa> getMpas() {
        return mpaDbStorage.getMpas();
    }

    public Mpa addNewMpa(Mpa mpa) {
        return mpaDbStorage.addNewMpa(mpa);
    }

    public Mpa updateMpa(Mpa mpa) {
        return mpaDbStorage.updateMpa(mpa);
    }

    public void removeMpaById(Integer id) {
        mpaDbStorage.removeMpaById(id);
    }
}

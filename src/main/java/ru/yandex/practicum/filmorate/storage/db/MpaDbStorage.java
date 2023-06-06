package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa getById(Integer id) {
        String sqlQuery = "select * " +
                "from mpa " +
                "where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id))
                    .orElseThrow(() -> new MpaNotFoundException("MPA-рейтинг с id " + id + " не найден"));
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException("MPA-рейтинг с id " + id + " не найден");
        }
    }

    public Collection<Mpa> getAll() {
        String sqlQuery = "select * " +
                "from mpa " +
                "order by id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    public Mpa addNew(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("mpa")
                .usingGeneratedKeyColumns("id");

        return getById(simpleJdbcInsert
                .executeAndReturnKey(Collections.singletonMap("name", mpa.getName()))
                .intValue());
    }

    public Mpa update(Mpa mpa) {
        String sqlQuery = "update mpa set " +
                "name = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                mpa.getName(),
                mpa.getId());
        return getById(mpa.getId());
    }

    public void removeById(Integer id) {
        String sqlQuery = "delete from mpa where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public Collection<Film> getFilms() {
        String sqlQuery = "select * " +
                "from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        String sqlQuery = "select * " +
                "from films " +
                "where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public Film addNewFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        genreDbStorage.updateFilmGenres(filmId, film);
        return getFilmById(filmId).get();
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, " +
                "description = ?, " +
                "date = ?, " +
                "duration = ?, " +
                "mpa_id = ?, " +
                "rate = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId());
        genreDbStorage.updateFilmGenres(film.getId(), film);
        return getFilmById(film.getId())
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + film.getId() + " не найден"));
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlQuery = "insert into film_likes " +
                "(film_id, user_id) " +
                "values ((select id from films where id = ?), " +
                "(select id from users where id = ?))";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("NULL not allowed for column \"FILM_ID\""))
                throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
            if (e.getMessage().contains("NULL not allowed for column \"USER_ID\""))
                throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String sqlQuery = "delete from film_likes " +
                "where film_id = ? and user_id = ?";
        if (!getFilmById(filmId).orElseThrow(() ->
                        new FilmNotFoundException("Фильм с id " + filmId + " не найден"))
                .getLikedUsers()
                .contains(userId)) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден в списке оценивших");
        }
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        String sqlQuery = "select f.* " +
                "from films f " +
                "left join film_likes as fl on f.id = fl.film_id " +
                "group by f.id " +
                "order by count(fl.user_id) desc, f.rate desc " +
                "limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Collection<Integer> getLikedUsersId(Integer filmId) {
        String sqlQuery = "select user_id " +
                "from film_likes " +
                "where film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("date").toLocalDate())
                .duration(Duration.ofSeconds(resultSet.getInt("duration")))
                .mpa(mpaDbStorage.getMpaById(resultSet.getInt("mpa_id")))
                .genres(genreDbStorage.getFilmGenres(resultSet.getInt("id")))
                .likedUsers(new HashSet<>(getLikedUsersId(resultSet.getInt("id"))))
                .rate(resultSet.getInt("rate"))
                .build();
    }
}
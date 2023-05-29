package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreById(Integer id) {
        String sqlQuery = "select * " +
                "from genres " +
                "where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id))
                    .orElseThrow(() -> new GenreNotFoundException("Жанр с id " + id + " не найден"));
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден");
        }
    }

    public Set<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "select * " +
                "from genres " +
                "where id in " +
                "(select genre_id " +
                "from film_genres " +
                "where film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId));
    }

    public void updateFilmGenres(Integer filmId, Film film) {
        removeFilmGenres(filmId);
        Set<Genre> genres = film.getGenres();
        String sqlQuery = "insert into film_genres " +
                "(film_id, genre_id) " +
                "values (?, ?)";
        if (genres != null) genres.forEach(g -> jdbcTemplate.update(sqlQuery, filmId, g.getId()));
    }

    private void removeFilmGenres(Integer filmId) {
        String sqlQuery = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    public Collection<Genre> getGenres() {
        String sqlQuery = "select * " +
                "from genres " +
                "order by id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre addNewGenre(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");

        return getGenreById(simpleJdbcInsert.executeAndReturnKey(Collections.singletonMap("name", genre.getName()))
                .intValue());
    }

    public Genre updateGenre(Genre genre) {
        String sqlQuery = "update genres set " +
                "name = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                genre.getName(),
                genre.getId());
        return getGenreById(genre.getId());
    }

    public void removeGenreById(Integer id) {
        String removeFromGenres = "delete from genres where id = ?";
        jdbcTemplate.update(removeFromGenres, id);
        String removeFilmGenreLink = "delete from film_genres where genre_id = ?";
        jdbcTemplate.update(removeFilmGenreLink, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}

package ru.yandex.practicum.filmorate.storageTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmStorageTest {
    private final FilmDbStorage filmStorage;
    private final ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void testAddNewFilm() throws JsonProcessingException {
        String json = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100,\n" +
                "  \"mpa\": { \"id\": 1, \"name\": \"G\"}\n" +
                "}";
        Film film = objectMapper.readValue(json, Film.class);
        assertThat(filmStorage.addNewFilm(film))
                .extracting(Film::getName,
                        Film::getDescription,
                        Film::getReleaseDate,
                        Film::getDuration,
                        Film::getMpa)
                .containsExactly(film.getName(),
                        film.getDescription(),
                        film.getReleaseDate(),
                        film.getDuration(),
                        film.getMpa());

        Film film2 = filmStorage.addNewFilm(film.toBuilder()
                .name("terminator")
                .description("destroy John Connor")
                .releaseDate(LocalDate.of(1984, 4, 11))
                .duration(Duration.ofMinutes(107))
                .mpa(Mpa.builder().id(4).name("R").build())
                .build());
        assertThat(film2)
                .extracting(Film::getName,
                        Film::getDescription,
                        Film::getReleaseDate,
                        Film::getDuration,
                        Film::getMpa)
                .containsExactly(film2.getName(),
                        film2.getDescription(),
                        film2.getReleaseDate(),
                        film2.getDuration(),
                        film2.getMpa());
    }

    @Test
    @Order(2)
    public void testUpdateFilm() {
        Film filmForUpdate = filmStorage.getFilmById(2).get()
                .toBuilder()
                .name("terminator 2")
                .description("save John Connor")
                .rate(4)
                .build();
        assertThat(filmStorage.updateFilm(filmForUpdate))
                .extracting(Film::getId,
                        Film::getName,
                        Film::getDescription,
                        Film::getReleaseDate,
                        Film::getDuration,
                        Film::getRate,
                        Film::getMpa)
                .containsExactly(filmForUpdate.getId(),
                        filmForUpdate.getName(),
                        filmForUpdate.getDescription(),
                        filmForUpdate.getReleaseDate(),
                        filmForUpdate.getDuration(),
                        filmForUpdate.getRate(),
                        filmForUpdate.getMpa());
    }

    @Test
    @Order(3)
    public void testGetFilms() {
        Collection<Film> films = filmStorage.getFilms();
        assertThat(films).asList().hasSize(2);
    }

    @Test
    @Order(4)
    public void testGetFilmById() {
        Optional<Film> film = filmStorage.getFilmById(1);
        assertThat(film)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", 1)
                );

        assertThatExceptionOfType(FilmNotFoundException.class)
                .isThrownBy(() ->
                        filmStorage.getFilmById(999))
                .withMessage("Фильм с id 999 не найден");
    }

    @Test
    @Order(5)
    public void testGetMostPopularFilm() {
        Film film = filmStorage.getFilmById(2).get();
        List<Film> filmSet = new ArrayList<>(Collections.singleton(film));

        assertEquals(filmStorage.getPopularFilms(1), filmSet);
    }
}

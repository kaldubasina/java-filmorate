package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenreStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    @Order(1)
    public void testGetGenres() {
        assertThat(genreDbStorage.getAll())
                .asList()
                .hasSize(6)
                .hasOnlyElementsOfType(Genre.class);
    }

    @Test
    @Order(2)
    public void testAddNewGenre() {
        Genre genre = Genre.builder().name("new Genre").build();
        assertThat(genreDbStorage.addNew(genre))
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(7, "new Genre");
    }

    @Test
    @Order(3)
    public void testGetGenreById() {
        assertThat(genreDbStorage.getById(7))
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(7, "new Genre");
    }

    @Test
    @Order(4)
    public void testUpdateGenre() {
        Genre genre = Genre.builder().id(7).name("updated Genre").build();
        assertThat(genreDbStorage.update(genre))
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(7, "updated Genre");
    }

    @Test
    @Order(5)
    public void testRemoveGenre() {
        genreDbStorage.removeById(1);
        assertThat(genreDbStorage.getAll())
                .asList()
                .hasSize(6)
                .doesNotContain(Genre.builder()
                        .id(1)
                        .name("Комедия")
                        .build()
                );
    }
}

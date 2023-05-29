package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MpaStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    @Order(1)
    public void testGetMpas() {
        assertThat(mpaDbStorage.getMpas())
                .asList()
                .hasSize(5)
                .hasOnlyElementsOfType(Mpa.class);
    }

    @Test
    @Order(2)
    public void testGetMpaById() {
        assertThat(mpaDbStorage.getMpaById(1))
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(1, "G");
    }

    @Test
    @Order(3)
    public void testAddNewMpa() {
        Mpa mpa = Mpa.builder().name("new Mpa").build();
        assertThat(mpaDbStorage.addNewMpa(mpa))
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(6, "new Mpa");
    }

    @Test
    @Order(4)
    public void testUpdateMpa() {
        Mpa mpa = Mpa.builder().id(6).name("updated Mpa").build();
        assertThat(mpaDbStorage.updateMpa(mpa))
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(6, "updated Mpa");
    }

    @Test
    @Order(5)
    public void testRemoveMpa() {
        mpaDbStorage.removeMpaById(6);
        assertThat(mpaDbStorage.getMpas())
                .asList()
                .hasSize(5)
                .doesNotContain(Mpa.builder()
                        .id(6)
                        .name("updated Mpa")
                        .build()
                );
    }
}

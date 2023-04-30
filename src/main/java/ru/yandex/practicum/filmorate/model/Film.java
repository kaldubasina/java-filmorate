package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.validators.StartDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"id"})
@Builder(toBuilder = true)
public class Film {
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;
    @StartDate(message = "Дата релиза - не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @DurationMin(seconds = 1, message = "Длительность должна быть положительная")
    private Duration duration;
    private Set<Integer> likedUsers = new HashSet<>();
}

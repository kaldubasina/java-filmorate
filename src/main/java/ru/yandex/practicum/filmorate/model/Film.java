package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationUnit;
import ru.yandex.practicum.filmorate.validators.StartDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;
    @StartDate(message = "Дата релиза - не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @DurationMin(minutes = 1, message = "Длительность должна быть положительная")
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration duration;
    private Mpa mpa;
    @JsonIgnore
    private Set<Integer> likedUsers;
    @Getter(AccessLevel.NONE)
    private Integer rate;
    private Set<Genre> genres;

    @JsonGetter("rate")
    public Integer getRate() {
        if (likedUsers == null || likedUsers.isEmpty()) {
            return rate;
        } else {
            return likedUsers.size();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("date", releaseDate);
        values.put("duration", duration);
        values.put("rate", getRate());
        values.put("mpa_id", mpa.getId());
        return values;
    }
}

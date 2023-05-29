package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    @PositiveOrZero
    private int id;
    @Email(message = "Электронная почта введена в неверном формате")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;
    @Getter(AccessLevel.NONE)
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    @JsonIgnore
    private Set<User> friends;

    @JsonGetter("name")
    public String getName() {
        if (name.isBlank()) return login;
        return name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", getName());
        values.put("birthday", birthday);
        return values;
    }
}

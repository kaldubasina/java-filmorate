package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> userMap = new HashMap<>();
    private static int id = 1;

    @GetMapping
    public List<User> getFilms() {
        return new ArrayList<>(userMap.values());
    }

    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        user.setId(id++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Добавлен новый пользователь: {}", user);
        userMap.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!userMap.containsKey(user.getId())) {
            log.debug("Пользователя с id {} не существует", user.getId());
            throw new NotFoundException("Пользователя с id " + user.getId() + " не существует");
        }
        log.debug("Пользователь с id {} обновлен", user.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.debug("Ошибка валидации - {}: {}", fieldName, errorMessage);
        });
        return errors;
    }
}

package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class FilmValidationTests {
	private static Validator validator;
	static {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.usingContext().getValidator();
	}

	@Test
	void validatorsTest() {
		final Film emptyFilm = Film.builder().build();
		Set<ConstraintViolation<Film>> validates = validator.validate(emptyFilm);
		Assertions.assertEquals(1, validates.size());
		Assertions.assertEquals("Название не может быть пустым", validates.stream()
				.iterator()
				.next()
				.getMessage());

		validates.clear();

		final Film film = emptyFilm.toBuilder()
				.name(" 	 ")
				.description("About film")
				.releaseDate(LocalDate.of(1895, 12, 27))
				.duration(Duration.ofMinutes(-130))
				.build();
		validates = validator.validate(film);
		Assertions.assertEquals(3, validates.size());
		List<String> errorMessages = new ArrayList<>();
		validates.stream()
				.map(ConstraintViolation::getMessage)
				.forEach(errorMessages::add);
		Assertions.assertTrue(errorMessages.contains("Название не может быть пустым"));
		Assertions.assertTrue(errorMessages.contains("Дата релиза - не раньше 28 декабря 1895 года"));
		Assertions.assertTrue(errorMessages.contains("Длительность должна быть положительная"));
	}
}

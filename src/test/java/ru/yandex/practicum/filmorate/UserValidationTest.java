package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserValidationTest {
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validatorsTest() {
        final User emptyUser = User.builder().build();
        Set<ConstraintViolation<User>> validates = validator.validate(emptyUser);
        Assertions.assertEquals(1, validates.size());
        Assertions.assertEquals("Логин не может быть пустым", validates.stream()
                .iterator()
                .next()
                .getMessage());

        validates.clear();

        final User user = emptyUser.toBuilder()
                .email("mail.Ru")
                .login("my login")
                .birthday(LocalDate.of(2095, 12, 27))
                .build();
        validates = validator.validate(user);
        Assertions.assertEquals(3, validates.size());
        List<String> errorMessages = new ArrayList<>();
        validates.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(errorMessages::add);
        Assertions.assertTrue(errorMessages.contains("Электронная почта введена в неверном формате"));
        Assertions.assertTrue(errorMessages.contains("Логин не может содержать пробелы"));
        Assertions.assertTrue(errorMessages.contains("Дата рождения не может быть в будущем"));
    }
}

package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.constraints.Past;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartDateValidator.class)
@Past
public @interface StartDate {
    String message() default "Дата должна быть позднее {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}

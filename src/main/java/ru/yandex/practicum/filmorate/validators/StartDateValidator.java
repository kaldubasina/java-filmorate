package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class StartDateValidator implements ConstraintValidator<StartDate, LocalDate> {
    private LocalDate startDate;

    @Override
    public void initialize(StartDate constraintAnnotation) {
        startDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(startDate);
    }
}

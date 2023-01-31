package ru.yandex.practicum.db;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateConstraintValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate != null && releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}

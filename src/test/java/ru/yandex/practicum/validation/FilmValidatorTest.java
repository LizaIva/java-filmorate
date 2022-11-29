package ru.yandex.practicum.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidatorTest {

    @Test
    public void validateNameTest() {
        Film film = new Film(null, "Человек, который бежит", LocalDate.of(2002, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("   ", "Человек, который бежит", LocalDate.of(2002, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film2 = new Film("", "Человек, который бежит", LocalDate.of(2002, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film3 = new Film("Бегущий по лезвию", "Человек, который бежит", LocalDate.of(2002, 12, 5), 120);
        assertDoesNotThrow(() -> FilmValidator.validate(film3));
    }

    @Test
    public void validateDescriptionTest() {
        Film film = new Film("Бегущий по лезвию", null, LocalDate.of(2002, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Ноябрь 2019 года. Бывший охотник на андроидов Рик " +
                "Декард восстановлен в полиции Лос-Анджелеса для поиска возглавляемой Роем Батти группы репликантов, " +
                "совершившей побег из космической колонии на Землю.Конец",
                LocalDate.of(2002, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));
    }

    @Test
    public void validateReleaseDateTest() {
        Film film = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(1801, 12, 5), 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Человек бежит", null, 120);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));
    }

    @Test
    public void validateDurationTest() {
        Film film = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), null);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), 0);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film2 = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), -10);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
    }

    @Test
    public void validateUpdatedTest() {
        Film updateFilm = new Film(null, null, null, null);
        updateFilm.setId(1);
        assertDoesNotThrow(() -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setName(" ");
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDescription("Ноябрь 2019 года. Бывший охотник на андроидов Рик Декард восстановлен в полиции Лос-Анджелеса " +
                "для поиска возглавляемой Роем Батти группы репликантов, совершившей побег из космической колонии на Землю.Конец");
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setReleaseDate(LocalDate.of(1789, 12, 5));
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDuration(0);
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDuration(-10);
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));
    }
}

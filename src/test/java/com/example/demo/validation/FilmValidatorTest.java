package com.example.demo.validation;

import com.example.demo.exception.ValidationException;
import com.example.demo.model.Film;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidatorTest {

    @Test
    public void validateNameTest() {
        Film film = new Film(null, "Человек, который бежит", LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("   ", "Человек, который бежит", LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film2 = new Film("", "Человек, который бежит", LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film3 = new Film("Бегущий по лезвию", "Человек, который бежит", LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertDoesNotThrow(() -> FilmValidator.validate(film3));
    }

    @Test
    public void validateDescriptionTest() {
        Film film = new Film("Бегущий по лезвию", null, LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Ноябрь 2019 года. Бывший охотник на андроидов Рик " +
                "Декард восстановлен в полиции Лос-Анджелеса для поиска возглавляемой Роем Батти группы репликантов, " +
                "совершившей побег из космической колонии на Землю.Конец",
                LocalDate.of(2002, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));
    }

    @Test
    public void validateReleaseDateTest() {
        Film film = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(1801, 12, 5), Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Человек бежит", null, Duration.ofMinutes(120));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));
    }

    @Test
    public void validateDurationTest() {
        Film film = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), null);
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));

        Film film1 = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), Duration.ofMinutes(0));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film1));

        Film film2 = new Film("Бегущий по лезвию", "Человек бежит", LocalDate.of(2001, 12, 5), Duration.ofMinutes(-10));
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film2));
    }

    @Test
    public void validateUpdatedTest() {
        Film updateFilm = new Film(null, null, null, null);
        assertDoesNotThrow(() -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setName(" ");
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDescription("Ноябрь 2019 года. Бывший охотник на андроидов Рик Декард восстановлен в полиции Лос-Анджелеса " +
                "для поиска возглавляемой Роем Батти группы репликантов, совершившей побег из космической колонии на Землю.Конец");
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setReleaseDate(LocalDate.of(1789, 12, 5));
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDuration(Duration.ofMinutes(0));
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));

        updateFilm.setDuration(Duration.ofMinutes(-10));
        assertThrows(ValidationException.class, () -> FilmValidator.validateForUpdate(updateFilm));
    }
}

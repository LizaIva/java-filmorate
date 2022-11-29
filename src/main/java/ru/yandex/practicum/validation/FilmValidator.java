package ru.yandex.practicum.validation;

import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;

import java.time.Duration;
import java.time.LocalDate;

public class FilmValidator {
    public static final LocalDate TIME_TO_START_FILM = LocalDate.of(1895, 12, 28);

    public static void validate(Film film) {
        if (film == null) {
            throw new ValidationException("Отсутсвуют данные для создания фильма");
        }
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    public static void validateForUpdate(Film film) {
        if (film == null) {
            throw new ValidationException("Отсутсвуют данные для обновления фильма");
        }
        if (film.getId() == null) {
            throw new ValidationException("Отсутсвует id фильма");
        }
        validateUpdatedName(film.getName());
        validateUpdatedDescription(film.getDescription());
        validateUpdatedDuration(film.getDuration());
        validateUpdatedReleaseDate(film.getReleaseDate());
    }

    private static void validateName(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }
    }

    private static void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isBefore(TIME_TO_START_FILM)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
        }
    }

    private static void validateDuration(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private static void validateUpdatedName(String name) {
        if (name != null && (name.isEmpty()|| name.isBlank())) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
    }

    private static void validateUpdatedDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }
    }

    private static void validateUpdatedDuration(Duration duration) {
        if (duration != null && (duration.isZero() || duration.isNegative())) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private static void validateUpdatedReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(TIME_TO_START_FILM)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
        }
    }
}

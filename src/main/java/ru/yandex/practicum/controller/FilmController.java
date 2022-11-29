package ru.yandex.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.validation.FilmValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.validation.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Map<Integer, Film> films = new HashMap();

    protected int counter = 0;

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        FilmValidator.validate(film);
        log.info("Произошло создание фильма");
        film.setId(++counter);
        films.put(film.getId(), film);
        return film;
    }


    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        FilmValidator.validateForUpdate(film);
        Integer id = film.getId();

        Film filmForUpdate = films.get(id);

        if (filmForUpdate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный фильм");
        }

        if (films.containsKey(id)) {
            log.info("Фильм найден, обновление фильма");

            if (film.getName() != null) {
                filmForUpdate.setName(film.getName());
            }

            if (film.getDescription() != null) {
                filmForUpdate.setDescription(film.getDescription());
            }

            if (film.getReleaseDate() != null) {
                filmForUpdate.setReleaseDate(film.getReleaseDate());
            }

            if (film.getDuration() != null) {
                filmForUpdate.setDuration(film.getDuration());
            }

        }
        return films.get(id);
    }


    @GetMapping(value = "/films")
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос списка всех фильмов.");
        return films.values();
    }
}


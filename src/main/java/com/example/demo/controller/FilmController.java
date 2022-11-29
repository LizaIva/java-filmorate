package com.example.demo.controller;

import com.example.demo.exception.ValidationException;
import com.example.demo.model.Film;
import com.example.demo.validation.FilmValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
        try {
            FilmValidator.validate(film);
            log.info("Произошло создание фильма");
            film.setId(counter++);
            films.put(film.getId(), film);
            return film;
        } catch (ValidationException e) {
            log.error("Не прошла валидация.", e);
            return null;
        }
    }


    @PutMapping(value = "/films/{id}")
    public void update(@PathVariable String id, @RequestBody Film film) {
        Integer idParsed = Integer.valueOf(id);
        try {
            FilmValidator.validateForUpdate(film);

            if (films.containsKey(idParsed)) {
                log.info("Фильм найден, обновление фильма");
                Film filmForUpdate = films.get(idParsed);
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
            } else {
                log.info("Фильм не найден, создание нового фильма");
                films.put(idParsed, film);
            }
        } catch (ValidationException e) {
            log.error("Не прошла валидация.", e);
        }
    }

    @GetMapping(value = "/films")
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос списка всех фильмов.");
        return films.values();
    }
}


package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.validation.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Произошло создание фильма");
        return filmStorage.put(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        FilmValidator.validateForUpdate(film);

        Film filmForUpdate = filmStorage.get(film.getId());

        if (filmForUpdate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный фильм");
        }


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

        return filmForUpdate;
    }


    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос списка всех фильмов.");
        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        log.info("Получен запрос фильма по id.");
        return filmStorage.get(Integer.valueOf(id));
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос, чтобы поставить лайк фильму {} по id пользователя {}.", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос на удаление лайка у фильма {} по id пользователя {}.", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopN(@RequestParam(name = "count", required = false, defaultValue = "10") String count) {
        log.info("Получен запрос на вывод топ {} фильмов", count);
        return filmService.getTop(Integer.valueOf(count));
    }

}


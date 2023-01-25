package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Произошло создание фильма");
        return filmService.put(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }


    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос списка всех фильмов.");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        log.info("Получен запрос фильма по id.");
        return filmService.get(Integer.valueOf(id));
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

    @GetMapping("/director/{id}")
    public List<Film> getFilmsSortedByDirector(@PathVariable Integer id, @RequestParam String sortBy){
        log.info("Запрос всех фильмов режиссёра, отсортированных по {}", sortBy);
        return filmService.getFilmDirectorSortedBy(id, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Integer userId,
                               @RequestParam(name = "friendId") Integer friendId) {
        log.info("Получен запрос на получение списка общих фильмов у пользователей {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{id}")
    public Film deleteById(@PathVariable int id) {
        return filmService.deleteById(id);
    }

}


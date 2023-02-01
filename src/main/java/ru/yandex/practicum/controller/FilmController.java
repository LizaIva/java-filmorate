package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Получен запрос на создание фильма");
        return filmService.put(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос на вывод списка всех фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        log.info("Получен запрос на вывод фильма с id = {}", id);
        return filmService.get(Integer.valueOf(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос на добавление лайка фильму c id = {} от пользователя c id = {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Получен запрос на удаление лайка у фильма с id = {} от пользователя с id = {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopN(@RequestParam(name = "count", required = false, defaultValue = "10") String count) {
        log.info("Получен запрос на вывод топа {} самых популярных фильмов", count);
        return filmService.getTop(Integer.valueOf(count));
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsSortedByDirector(@PathVariable Integer id, @RequestParam String sortBy) {
        log.info("Получен запрос на вывод всех фильмов режиссёра с id = {}, отсортированных по {}", id, sortBy);
        return filmService.getFilmDirectorSortedBy(id, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Integer userId,
                                     @RequestParam(name = "friendId") Integer friendId) {
        log.info("Получен запрос на вывод списка общих фильмов пользователей c id равными {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("Получен запрос на вывод списка фильмов по запросу {} и {}", query, by);
        return filmService.searchFilms(query, by);
    }

    @DeleteMapping("/{id}")
    public Film deleteById(@PathVariable int id) {
        log.info("Получен запрос на удаление фильма с id = {}", id);
        return filmService.deleteById(id);
    }

    @GetMapping(value = "/popular", params = {"count", "genreId", "year"})
    public List<Film> findLimitPopularFilmsByGenreAndYear(@RequestParam(value = "count") Integer count,
                                                          @RequestParam(value = "genreId") Integer genreId,
                                                          @RequestParam(value = "year") Integer year) {

        log.info("Получен запрос на вывод топ {} фильмов в жанре {} за {} год", count, genreId, year);
        return filmService.findLimitPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping(value = "/popular", params = {"year", "genreId"})
    public List<Film> findPopularFilmsByYearAndGenre(@RequestParam(value = "year") Integer year,
                                                     @RequestParam(value = "genreId") Integer genreId) {

        log.info("Получен запрос на вывод топ фильмов в жанре {} за {} год", genreId, year);
        return filmService.findPopularFilmsByYearAndGenre(year, genreId);
    }

    @GetMapping(value = "/popular", params = {"year"})
    public List<Film> findPopularFilmsByYear(@RequestParam(value = "year") Integer year) {

        log.info("Получен запрос на вывод топ фильмов за {} год", year);
        return filmService.findPopularFilmsByYear(year);
    }

    @GetMapping(value = "/popular", params = {"genreId"})
    public List<Film> findPopularFilmsByGenre(@RequestParam(value = "genreId") Integer genreId) {

        log.info("Получен запрос на вывод топ фильмов в жанре {}", genreId);
        return filmService.findPopularFilmsByGenre(genreId);
    }
}
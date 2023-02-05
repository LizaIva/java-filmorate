package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение списка всех жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("Получен запрос на получение жанра с id = {}", id);
        return filmService.getGenreById(id);
    }
}

package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.service.FilmService;

import java.util.List;

@RestController
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres(){
        log.info("Получен запрос на получение списка всех жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id){
        log.info("Получен запрос на получение жанра по его id");
        return filmService.getGenreById(id);
    }
}

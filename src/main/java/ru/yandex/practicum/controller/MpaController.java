package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.service.FilmService;

import java.util.List;

@RestController
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/mpa")
    public List<MPA> getAllCategories(){
        log.info("Получен запрос на получение списка всех категорий фильмов");
        return filmService.getAllCategories();
    }

    @GetMapping("/mpa/{id}")
    public MPA getCategoryById(@PathVariable Integer id){
        log.info("Получен запрос на получение категории по его id");
        return filmService.getCategoryById(id);
    }
}

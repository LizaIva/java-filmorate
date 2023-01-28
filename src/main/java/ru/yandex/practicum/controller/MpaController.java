package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping("/mpa")
    public List<MPA> getAllCategories() {
        log.info("Получен запрос на получение списка всех категорий фильмов");
        return filmService.getAllCategories();
    }

    @GetMapping("/mpa/{id}")
    public MPA getCategoryById(@PathVariable Integer id) {
        log.info("Получен запрос на получение категории с id = {}", id);
        return filmService.getCategoryById(id);
    }
}

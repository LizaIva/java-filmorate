package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director add(@RequestBody @Valid Director director) {
        log.info("Получен запрос на создание режессера {}", director.getName());
        return directorService.addDirector(director);
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Integer id) {
        log.info("Получен запрос на получение режессера с id = {}", id);
        return directorService.getDirector(id);
    }

    @GetMapping
    public List<Director> findAll() {
        log.info("Получен запрос на получение всех режессеров");
        return directorService.getAllDirectors();
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director director) {
        log.info("Получен запрос на обновление данных режессера с id = {}", director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public int deleteById(@PathVariable Integer id) {
        log.info("Получен запрос на удаление режессера с id = {}", id);
        return directorService.deleteDirector(id);
    }
}

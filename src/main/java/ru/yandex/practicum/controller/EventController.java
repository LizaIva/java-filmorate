package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.model.event.Event;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{id}/feed")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<Event> getEvents(@PathVariable Integer id) {
        log.info("Получен запрос ленты событий по id пользователя {}.", id);
        return eventService.getEvents(id);
    }
}

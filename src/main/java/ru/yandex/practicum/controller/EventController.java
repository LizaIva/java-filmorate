package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.model.event.Event;

import java.util.List;


@RestController
@RequestMapping("/users/{id}/feed")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getEvents(@PathVariable Integer userId) {
        log.info("Получен запрос ленты событий по id пользователя {}.", userId);
        return eventService.getEvents(userId);
    }
}

package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;

import java.util.List;

public interface EventStorage {
    void putEvent(Integer userId, EventType eventType, Operation operation, Integer entityId);

    List<Event> getEvents(Integer userId);
}

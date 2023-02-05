package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;
import ru.yandex.practicum.storage.EventStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userDbStorage;


    public List<Event> getEvents(Integer userId) {
        userDbStorage.checkUser(userId);
        return eventStorage.getEvents(userId);
    }

    public void putEvent(Integer userId, EventType eventType, Operation operation, Integer entityId) {
        eventStorage.putEvent(userId, eventType, operation, entityId);
    }

}

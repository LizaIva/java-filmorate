package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.storage.db.EventDbStorage;
import ru.yandex.practicum.storage.db.UserDbStorage;

import java.util.List;

@Service
public class EventService {

    private final EventDbStorage eventDbStorage;
    private final UserDbStorage userDbStorage;

    public EventService(EventDbStorage eventDbStorage, UserDbStorage userDbStorage) {
        this.eventDbStorage = eventDbStorage;
        this.userDbStorage = userDbStorage;
    }


    public List<Event> getEvents(Integer userId){
        userDbStorage.checkUser(userId);
        return eventDbStorage.getEvents(userId);
    }

}

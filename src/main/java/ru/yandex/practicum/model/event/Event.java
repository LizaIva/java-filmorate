package ru.yandex.practicum.model.event;

import lombok.Data;


@Data
public class Event {
    private Integer eventId;
    private Integer timestamp;

    private Integer userId;

    private EventType eventType;

    private Operation operation;

    private Integer entityId;

    public Event(int timestamp, int userId, EventType eventType, Operation operation, int entityId) {
    }
}


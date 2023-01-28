package ru.yandex.practicum.model.event;

import lombok.Data;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;


@Data
public class Event {
    private Integer eventId;
    private Long timestamp;

    private Integer userId;

    private EventType eventType;

    private Operation operation;

    private Integer entityId;

    public Event(long timestamp, int userId, EventType eventType, Operation operation, int entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}


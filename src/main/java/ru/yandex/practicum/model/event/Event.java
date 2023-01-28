package ru.yandex.practicum.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;


@Data
@AllArgsConstructor
public class Event {
    private Integer eventId;
    private Long timestamp;

    private Integer userId;

    private EventType eventType;

    private Operation operation;

    private Integer entityId;
}


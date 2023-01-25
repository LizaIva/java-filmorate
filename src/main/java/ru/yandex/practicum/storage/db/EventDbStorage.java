package ru.yandex.practicum.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.event.EventType;
import ru.yandex.practicum.model.event.Operation;
import ru.yandex.practicum.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class EventDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void putEvent(Integer userId, EventType eventType, Operation operation, Integer entityId){
        String sqlQuery = "insert into event_feed (user_id, event_type, operation, entity_id) " +
                "values (?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, userId, eventType.name(), operation.name(), entityId);
    }

    public List<Event> getEvents(Integer userId){
        List<Event> foundEvent = jdbcTemplate.query(
                "select * from " +
                        "(select * from EVENT_FEED where user_id = ? ORDER BY TIMESTAMP DESC) as user_evenets," +
                        "(select * from EVENT_FEED where user_id in (select friend_id from USER_FRIENDS where USER_FRIENDS.USER_ID = ?) ORDER BY TIMESTAMP DESC) as frineds_events;",
                (rs, rowNum) -> mapEventData(rs),
                userId,
                userId
        );

        if (foundEvent == null) {
            return null;
        }


        return foundEvent;
    }

    private Event mapEventData(ResultSet rs) throws SQLException {
        Event event = new Event(
                rs.getInt("timestamp"),
                rs.getInt("user_id"),
                rs.getObject("event_type", EventType.class),
                rs.getObject("operation", Operation.class),
                rs.getInt("entity_id")
        );

        event.setEventId(rs.getInt("event_id"));
        return event;
    }

}

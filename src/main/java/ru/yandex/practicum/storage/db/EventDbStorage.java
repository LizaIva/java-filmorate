package ru.yandex.practicum.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;
import ru.yandex.practicum.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putEvent(Integer userId, EventType eventType, Operation operation, Integer entityId) {
        String sqlQuery = "insert into event_feed (user_id, event_type, operation, entity_id) " +
                "values (?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, userId, eventType.name(), operation.name(), entityId);
    }

    @Override
    public List<Event> getEvents(Integer userId) {
        return jdbcTemplate.query(
                "select * from EVENT_FEED where user_id = ? " +
                        "union " +
                        "select * " +
                        "from EVENT_FEED " +
                        "where user_id in (select friend_id from USER_FRIENDS where USER_FRIENDS.USER_ID = ? and STATUS_ID = 0) " +
                        "ORDER BY TIMESTAMP",
                (rs, rowNum) -> mapEventData(rs),
                userId,
                userId
        );
    }

    private Event mapEventData(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("event_id"),
                rs.getTimestamp("timestamp").getTime(),
                rs.getInt("user_id"),
                EventType.valueOf(rs.getString("event_type")),
                Operation.valueOf(rs.getString("operation")),
                rs.getInt("entity_id")
        );
    }

}

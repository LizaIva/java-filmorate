package ru.yandex.practicum.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.FriendConnection;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Override
    public User put(User user) {
        String sqlQuery = "insert into users (email, login, name, birthday) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery,
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, new java.sql.Date(Date.valueOf(user.getBirthday()).getTime()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    public User updateUser(User user) {
        User userForUpdate = get(user.getId());

        StringBuilder query = new StringBuilder("update users set ");
        List<Object> args = new LinkedList<>();

        if (user.getEmail() != null) {
            query.append("EMAIL = ?");
            args.add(user.getEmail());

            userForUpdate.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("LOGIN = ?");
            args.add(user.getLogin());

            userForUpdate.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("NAME = ?");
            args.add(user.getName());

            userForUpdate.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("BIRTHDAY = ?");
            args.add(user.getBirthday());

            userForUpdate.setBirthday(user.getBirthday());
        }

        if (!args.isEmpty()) {
            query.append(" where USER_ID = ?");
            args.add(user.getId());

            jdbcTemplate.update(query.toString(), args.toArray(Object[]::new));
        }

        return userForUpdate;
    }

    @Override
    public User get(Integer id) {
        User foundUser = jdbcTemplate.query(
                "select * from users where user_id = ?",
                rs -> {
                    if (!rs.first() && !rs.next()) {
                        throw new UnknownDataException("Пользователь с данным id не существует");
                    }
                    return mapUserData(rs);
                },
                id
        );

        if (foundUser == null) {
            return null;
        }

        foundUser.setFriends(findFriends(id));

        return foundUser;
    }

    @Override
    public User deleteById(int id) {
        String sqlDeleteQuery = "DELETE FROM users WHERE USER_ID = ?";
        User user = get(id);

        jdbcTemplate.update(sqlDeleteQuery, id);
        log.info("Запрос на удаление user с id = {} отправлен", id);

        return user;
    }

    private List<FriendConnection> findFriends(Integer id) {
        return jdbcTemplate.query(
                "select us.friend_id, s.name from user_friends us JOIN status s ON us.status_id = s.STATUS_ID WHERE USER_ID = ?",
                (rs, rowNum) -> {
                    FriendConnection friendConnection = new FriendConnection();
                    friendConnection.setFriendId(rs.getInt("friend_id"));
                    friendConnection.setStatus(rs.getString("name"));

                    return friendConnection;
                },
                id
        );
    }


    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        return jdbcTemplate.query(
                String.format("select * from users where user_id in (%s)", repeat(ids.size(), ",")),
                (rs, rowNum) -> {
                    User user = mapUserData(rs);
                    user.setFriends(findFriends(user.getId()));
                    return user;
                },
                ids.toArray()
        );
    }


    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("select * from users",
                (rs, rowNum) -> {
                    User user = mapUserData(rs);
                    user.setFriends(findFriends(user.getId()));
                    return user;
                }
        );
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "insert into user_friends (user_id, friend_id) " +
                "values (?, ?)";

        jdbcTemplate.update(
                sqlQuery,
                userId,
                friendId
        );
    }

    @Override
    public void acceptFriendship(int userId, int friendId) {
        User friend = get(friendId);
        boolean notFriend = true;

        for (FriendConnection friendConnection : friend.getFriends()) {
            if (friendConnection.getFriendId() == userId) {
                notFriend = false;
                break;
            }
        }

        if (notFriend) {
            throw new UnknownDataException("Данной связи не существует");
        }

        jdbcTemplate.update(
                "UPDATE USER_FRIENDS SET STATUS_ID = 0 WHERE USER_ID = ? AND FRIEND_ID = ?",
                friendId,
                userId
        );

        jdbcTemplate.update(
                "insert into user_friends (user_id, friend_id, STATUS_ID) " +
                        "values (?, ?, 0)",
                userId,
                friendId
        );
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sqlQuery = "delete from user_friends where user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
        jdbcTemplate.update(sqlQuery, friendId, userId);
    }

    @Override
    public List<User> foundCommonFriends(int userId1, int userId2) {
        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT friend_id " +
                "                  FROM user_friends " +
                "      WHERE user_id=? " +
                "      AND friend_id IN " +
                "          (SELECT friend_id " +
                "                       FROM user_friends " +
                "                       WHERE user_id=?));";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {
                    User user = mapUserData(rs);
                    user.setFriends(findFriends(user.getId()));
                    return user;
                },
                userId1,
                userId2
        );
    }

    @Override
    public List<User> foundUserFriends(int userId) {
        String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT friend_id " +
                "                  FROM user_friends " +
                "                   WHERE user_id=?)";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {
                    User user = mapUserData(rs);
                    user.setFriends(findFriends(user.getId()));
                    return user;
                },
                userId);
    }

    @Override
    public String getStatusName(int statusId) {
        String sqlQuery = "SELECT name FROM status WHERE STATUS_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, String.class, statusId);
    }

    @Override
    public List<Film> getRecommendations(Integer userId) {
        checkUser(userId);

        Map<Integer, HashMap<Integer, Double>> data = new HashMap<>();
        String sqlQuery = "SELECT user_id, film_id, mark FROM film_likes;";

        Map<Integer, HashMap<Integer, Double>> rows = jdbcTemplate.query(sqlQuery, this::mapUserMark);

        if (rows != null && rows.size() != 0) {
            for (Map.Entry<Integer, HashMap<Integer, Double>> rowsEntry : rows.entrySet()) {
                HashMap<Integer, Double> hashMap = rowsEntry.getValue();

                for (Map.Entry<Integer, Double> filmMark : hashMap.entrySet()) {
                    int user = rowsEntry.getKey();

                    if (!data.containsKey(user)) {
                        data.put(user, new HashMap<>());
                    }

                    data.get(user).put(filmMark.getKey(), filmMark.getValue());
                }
            }
        } else {
            return new ArrayList<>();
        }

        Map<Integer, HashMap<Integer, Double>> diff = new HashMap<>();
        Map<Integer, HashMap<Integer, Integer>> freq = new HashMap<>();

        for (HashMap<Integer, Double> user : data.values()) {
            for (Map.Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }

                for (Map.Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }

                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }

                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }

        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }

        HashMap<Integer, Double> uPred = new HashMap<>();
        HashMap<Integer, Integer> uFreq = new HashMap<>();

        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }

        Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Double>> user : data.entrySet()) {
            for (Integer j : user.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j) + user.getValue().get(j);
                        double finalValue = predictedValue * freq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                    } catch (NullPointerException ignored) {
                    }
                }
            }

            HashMap<Integer, Double> clean = new HashMap<>();

            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }

            List<Integer> items = filmDbStorage.getAll().stream()
                    .map(Film::getId)
                    .collect(Collectors.toList());

            for (Integer j : items) {
                if (user.getValue().containsKey(j)) {
                    clean.put(j, user.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }

            outputData.put(user.getKey(), clean);
        }

        List<Integer> recommendedFilmsIds = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : outputData.get(userId).entrySet()) {
            if (entry.getValue() > 5.0 && !getLikedFilmsIds(userId).contains(entry.getKey())) {
                recommendedFilmsIds.add(entry.getKey());
            }
        }

        return recommendedFilmsIds.stream()
                .map(filmDbStorage::get)
                .collect(Collectors.toList());
    }

    private List<Integer> getLikedFilmsIds(Integer userId) {
        String sqlQuery = "SELECT film_id FROM film_likes WHERE user_id = ?;";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    private static String repeat(int times, String delimiter) {
        if (times == 1) {
            return "?";
        }

        String withDelimiter = "?" + delimiter;

        return withDelimiter.repeat(times - 1) + "?";
    }

    private User mapUserData(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getObject("birthday", LocalDate.class)
        );

        user.setId(rs.getInt("user_id"));

        return user;
    }

    private Map<Integer, HashMap<Integer, Double>> mapUserMark(ResultSet rs) throws SQLException {
        Map<Integer, HashMap<Integer, Double>> rows = new HashMap<>();

        while (rs.next()) {
            int userId = rs.getInt("user_id");

            if (!rows.containsKey(userId)) {
                rows.put(userId, new HashMap<>());
            }

            rows.get(userId).put(rs.getInt("film_id"), (double) rs.getInt("mark"));
        }

        return rows;
    }

    @Override
    public void checkUser(int id) {
        final String sqlCheckQuery = "SELECT * FROM users WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlCheckQuery, id);

        if (!userRows.next()) {
            log.info("user с id = {} не найден.", id);
            throw new UnknownDataException("user с id = " + id + " не найден.");
        }
    }
}

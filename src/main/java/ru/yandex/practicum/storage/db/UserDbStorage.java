package ru.yandex.practicum.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.user.FriendConnection;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

        if (user.getEmail() != null) {
            jdbcTemplate.update("update users set EMAIL = ? where USER_ID = ?",
                    user.getEmail(),
                    user.getId()
            );
            userForUpdate.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            jdbcTemplate.update("update users set LOGIN = ? where USER_ID = ?",
                    user.getLogin(),
                    user.getId()
            );
            userForUpdate.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            jdbcTemplate.update("update users set NAME = ? where USER_ID = ?",
                    user.getName(),
                    user.getId()
            );
            userForUpdate.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            jdbcTemplate.update("update users set BIRTHDAY = ? where USER_ID = ?",
                    user.getBirthday(),
                    user.getId()
            );
            userForUpdate.setBirthday(user.getBirthday());
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
        get(userId);
        get(friendId);

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
        User user = get(userId);
        User friend = get(friendId);

        boolean notFriend = true;
        for (FriendConnection friendConnection : friend.getFriends()) {
            if (friendConnection.getFriendId() == user.getId()) {
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

        jdbcTemplate.update(
                sqlQuery,
                userId,
                friendId
        );

        jdbcTemplate.update(
                sqlQuery,
                friendId,
                userId
        );

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
}

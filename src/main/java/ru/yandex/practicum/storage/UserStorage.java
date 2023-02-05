package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;

import java.util.List;

public interface UserStorage {

    User put(User user);

    User updateUser(User user);

    User get(Integer id);

    User deleteById(int id);

    List<User> getUsersByIds(List<Integer> ids);

    List<User> getAll();

    void addFriend(int userId, int friendId);

    void acceptFriendship(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> foundCommonFriends(int userId1, int userId2);

    List<User> foundUserFriends(int userId);

    String getStatusName(int statusId);

    void checkUser(int id);

    List<Film> getRecommendations(Integer userId);
}

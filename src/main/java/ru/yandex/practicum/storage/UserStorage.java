package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {

    User put(User user);

    User get(Integer id);

    List<User> getUsersByIds(List<Integer> ids);

    List<User> getAll();
}

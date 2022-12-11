package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    protected int counter = 0;

    @Override
    public User put(User user) {
        if (user == null) {
            throw new UnknownDataException("Нельзя сохранить пустого пользователя");
        }

        if (user.getId() == null) {
            user.setId(++counter);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Integer id) {
        User user = users.get(id);

        if (user == null) {
            throw new UnknownDataException("Пользователь с данным id не существует");
        }
        return user;
    }

    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        List<User> usersList = new ArrayList<>();
        for (Integer id : ids) {
            usersList.add(users.get(id));
        }
        return usersList;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }


}

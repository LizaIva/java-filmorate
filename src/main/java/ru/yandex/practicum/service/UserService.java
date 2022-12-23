package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.validation.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User put(User user) {
        return userStorage.put(user);
    }

    public User update(User user) {
        UserValidator.validateForUpdate(user);

        User userForUpdate = userStorage.get(user.getId());

        if (user.getEmail() != null) {
            userForUpdate.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            userForUpdate.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            userForUpdate.setBirthday(user.getBirthday());
        }

        return userForUpdate;
    }

    public User get(Integer id) {
        return userStorage.get(id);
    }

    public List<User> getUsersByIds(List<Integer> ids) {
        return userStorage.getUsersByIds(ids);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }


    public void addFriends(int userId, int addedUserId) {
        User user = userStorage.get(userId);
        User addedUser = userStorage.get(addedUserId);

        user.getFriendsId().add(addedUser.getId());
        addedUser.getFriendsId().add(user.getId());
    }

    public void removeFriends(int userId, int removedUserid) {
        User user = userStorage.get(userId);
        User removedUser = userStorage.get(removedUserid);

        if (!user.getFriendsId().contains(removedUser.getId())) {
            throw new UnknownDataException("Пользователя с данным id нет в списке друзей");
        }

        if (!removedUser.getFriendsId().contains(user.getId())) {
            throw new UnknownDataException("Пользователя с данным id нет в списке друзей");
        }

        user.getFriendsId().remove(removedUser.getId());
        removedUser.getFriendsId().remove(user.getId());
    }


    public List<User> commonFriends(int userId1, int userId2) {
        User user1 = userStorage.get(userId1);
        User user2 = userStorage.get(userId2);

        List<Integer> common = new ArrayList<>(user1.getFriendsId());
        common.retainAll(user2.getFriendsId());

        return userStorage.getUsersByIds(common);
    }

    public List<User> getAllFriends(int userId) {
        User user = userStorage.get(userId);
        List<User> allFriends = new ArrayList<>();
        for (Integer id : user.getFriendsId()) {
            allFriends.add(userStorage.get(id));
        }
        return allFriends;
    }
}

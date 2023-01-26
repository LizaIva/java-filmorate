package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.event.EventType;
import ru.yandex.practicum.model.event.Operation;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.storage.db.EventDbStorage;
import ru.yandex.practicum.validation.UserValidator;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final EventDbStorage eventDbStorage;

    public UserService(UserStorage userStorage, EventDbStorage eventDbStorage) {
        this.userStorage = userStorage;
        this.eventDbStorage = eventDbStorage;
    }

    public User put(User user) {
        if (user == null) {
            throw new UnknownDataException("Нельзя сохранить пустого пользователя");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.put(user);
    }

    public User update(User user) {
        UserValidator.validateForUpdate(user);

        return userStorage.updateUser(user);
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

    public User deleteById(int id) {
        userStorage.checkUser(id);
        return userStorage.deleteById(id);
    }

    public void addFriends(int userId, int addedUserId) {
        userStorage.checkUser(userId);
        userStorage.checkUser(addedUserId);
        userStorage.addFriend(userId, addedUserId);
        eventDbStorage.putEvent(userId, EventType.FRIEND, Operation.ADD, addedUserId);
    }

    public void acceptFriendship(int userId, int friendId) {
        userStorage.checkUser(userId);
        userStorage.checkUser(friendId);
        userStorage.acceptFriendship(userId, friendId);
        eventDbStorage.putEvent(userId, EventType.FRIEND, Operation.UPDATE, friendId);
    }

    public String getStatusName(int statusId) {
        return userStorage.getStatusName(statusId);
    }

    public void removeFriends(int userId, int removedUserid) {
        userStorage.checkUser(userId);
        userStorage.checkUser(removedUserid);
        userStorage.removeFriend(userId, removedUserid);
        eventDbStorage.putEvent(userId, EventType.FRIEND, Operation.REMOVE, removedUserid);
    }

    public List<User> commonFriends(int userId1, int userId2) {
        userStorage.checkUser(userId1);
        userStorage.checkUser(userId2);
        return userStorage.foundCommonFriends(userId1, userId2);
    }

    public List<User> getAllFriends(int userId) {
        userStorage.checkUser(userId);
        return userStorage.foundUserFriends(userId);
    }

}

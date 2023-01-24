package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.user.User;
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
        userStorage.addFriend(userId, addedUserId);
    }

    public void acceptFriendship(int userId, int friendId){
        userStorage.acceptFriendship(userId, friendId);
    }

    public String getStatusName(int statusId){
        return userStorage.getStatusName(statusId);
    }

    public void removeFriends(int userId, int removedUserid) {
        userStorage.removeFriend(userId, removedUserid);
    }

    public List<User> commonFriends(int userId1, int userId2) {
        return userStorage.foundCommonFriends(userId1, userId2);
    }

    public List<User> getAllFriends(int userId) {
       return userStorage.foundUserFriends(userId);
    }

}

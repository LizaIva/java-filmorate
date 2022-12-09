package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.validation.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Произошло создание пользователя");
        return userStorage.put(user);
    }


    @PutMapping
    public User update(@RequestBody User user) {
        UserValidator.validateForUpdate(user);

        User userForUpdate = userStorage.get(user.getId());

        if (userForUpdate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неизвестный пользователь");
        }

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

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос списка всех пользователей.");
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Получен запрос пользователя по id.");
        return userStorage.get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос добавления пользователя в друзья.");
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос добавления удаления пользователя из друзей.");
        userService.removeFriends(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getAllFriendsByUserId(@PathVariable Integer id) {
        log.info("Получен запрос на вывод всех друзей пользователя по его id.");
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получен запрос на вывод общих друзей 2-х пользователей.");
        return userService.commonFriends(id, otherId);
    }
}

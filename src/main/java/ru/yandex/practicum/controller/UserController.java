package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Произошло создание пользователя");
        return userService.put(user);
    }


    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос списка всех пользователей.");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Получен запрос пользователя по id.");
        return userService.get(id);
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

    @PatchMapping("/{userId}/accept-friendship/{friendId}")
    public void acceptFriendship(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Пользователь добавлен в друзья.");
        userService.acceptFriendship(userId, friendId);
    }

    @GetMapping("/status/{statusId}")
    public String getStatusName(@PathVariable Integer statusId) {
        log.info("Получение названия статуса дружбы.");
        return userService.getStatusName(statusId);
    }
}

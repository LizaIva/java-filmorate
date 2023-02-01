package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.put(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос на вывод списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Получен запрос на вывод пользователя с id = {}", id);
        return userService.get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос на добавления пользователя с id = {} в друзья пользователя с id = {}", friendId, id);
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление пользователя с id = {} из друзей пользователя с id = {}", friendId, id);
        userService.removeFriends(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getAllFriendsByUserId(@PathVariable Integer id) {
        log.info("Получен запрос на вывод всех друзей пользователя с id = {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получен запрос на вывод общих друзей 2-х пользователей - с id равными {} и {}", id, otherId);
        return userService.commonFriends(id, otherId);
    }

    @PatchMapping("/{userId}/accept-friendship/{friendId}")
    public void acceptFriendship(@PathVariable Integer userId, @PathVariable Integer friendId) {
        log.info("Получен запрос на добавление пользователя с id = {} в друзья пользователя с id = {}", friendId, userId);
        userService.acceptFriendship(userId, friendId);
    }

    @GetMapping("/status/{statusId}")
    public String getStatusName(@PathVariable Integer statusId) {
        log.info("Получен запрос на вывод названия статуса дружбы с id = {}", statusId);
        return userService.getStatusName(statusId);
    }

    @DeleteMapping("/{id}")
    public User deleteById(@PathVariable int id) {
        log.info("Получен запрос на удаление пользоваля с id = {}", id);
        return userService.deleteById(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable(name = "id") Integer id) {
        log.info("Получен запрос на генерацию рекомендаций для пользователя с id = {}", id);
        return userService.getRecommendations(id);
    }
}

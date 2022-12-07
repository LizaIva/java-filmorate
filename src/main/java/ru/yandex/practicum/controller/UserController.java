package ru.yandex.practicum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.validation.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap();
    protected int counter = 0;


    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Произошло создание пользователя");
        user.setId(++counter);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }


    @PutMapping
    public User update(@RequestBody User user) {
        UserValidator.validateForUpdate(user);
        Integer id = user.getId();

        User userForUpdate = users.get(id);

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
        return users.values();
    }
}

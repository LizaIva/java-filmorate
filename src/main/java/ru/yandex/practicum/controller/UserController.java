package ru.yandex.practicum.controller;

import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap();
    protected int counter = 0;


    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        UserValidator.validate(user);
        log.info("Произошло создание пользователя");
        user.setId(++counter);
        users.put(user.getId(), user);
        return user;
    }


    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        UserValidator.validateForUpdate(user);
        Integer id = user.getId();

        if (users.containsKey(id)) {
            User userForUpdate = users.get(id);
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
        } else {
            log.info("Произошло обновление пользователя");
            users.put(id, user);
        }
        return users.get(id);
    }

    @GetMapping(value = "/users")
    public Collection<User> findAllUsers() {
        log.info("Получен запрос списка всех пользователей.");
        return users.values();
    }
}

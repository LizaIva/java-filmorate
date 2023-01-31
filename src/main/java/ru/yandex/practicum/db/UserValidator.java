package ru.yandex.practicum.db;

import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.user.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) {
        if (user == null) {
            throw new ValidationException("Отсутсвуют данные для создания пользователя");
        }
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateName(user);
        validateBirthday(user.getBirthday());
    }

    public static void validateForUpdate(User user) {
        if (user == null) {
            throw new ValidationException("Отсутсвуют данные для создания пользователя");
        }

        if (user.getId() == null) {
            throw new ValidationException("Отсутсвует id пользователя");
        }

        validateUpdatedEmail(user.getEmail());
        validateUpdatedLogin(user.getLogin());
        validateUpdatedName(user);
        validateUpdatedBirthday(user.getBirthday());
    }

    private static void validateEmail(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать @");
        }
    }

    private static void validateLogin(String login) {
        if (login == null || login.isEmpty() || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private static void validateName(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private static void validateUpdatedEmail(String email) {
        if (email != null && (email.isEmpty() || !email.contains("@"))) {
            throw new ValidationException("Email не может быть пустым и должен содержать @");
        }
    }

    private static void validateUpdatedLogin(String login) {
        if (login != null && (login.isEmpty() || login.isBlank() || login.contains(" "))) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private static void validateUpdatedName(User user) {
        String name = user.getName();
        if (name != null && (name.isEmpty() || name.isBlank())) {
            user.setName(user.getLogin());
        }
    }

    private static void validateUpdatedBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
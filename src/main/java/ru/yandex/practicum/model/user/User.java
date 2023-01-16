package ru.yandex.practicum.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "friends")
public class User {

    private Integer id;
    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Введнный email не правильного формата")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private List<FriendConnection> friends = new ArrayList<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

}

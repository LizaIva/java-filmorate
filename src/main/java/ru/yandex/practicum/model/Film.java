package ru.yandex.practicum.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ru.yandex.practicum.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "userLikes")
public class Film {

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Количество символов не может превышать 200")
    private String description;

    @PastOrPresent(message = "Дата релиза не может быть в будущем времени")
    @FilmReleaseDate(message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;
    @NonNull
    @Positive
    private Integer duration;

    private Set<Integer> userLikes = new HashSet<>();

    public void addUserLike(int userId) {
        userLikes.add(userId);
    }

    public void removeUserLike(int userId) {
        userLikes.remove(userId);
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}

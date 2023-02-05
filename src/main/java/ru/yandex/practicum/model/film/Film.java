package ru.yandex.practicum.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import ru.yandex.practicum.validation.FilmReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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

    @NotNull
    private MPA mpa;

    private List<Genre> genres;

    private List<Director> directors;

    public Film(String name, String description, LocalDate releaseDate, Integer duration, MPA mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    private Integer middleRating;
}

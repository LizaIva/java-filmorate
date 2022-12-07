package ru.yandex.practicum.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
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

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }


}

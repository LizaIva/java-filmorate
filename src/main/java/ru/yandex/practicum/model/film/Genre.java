package ru.yandex.practicum.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Genre {

    private Integer id;

    private String name;

    public Genre() {
    }

    public Genre(Integer id) {
        this.id = id;
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}

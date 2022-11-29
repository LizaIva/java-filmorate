package ru.yandex.practicum.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    public Film(String name, String description, LocalDate releaseDate, Duration duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}

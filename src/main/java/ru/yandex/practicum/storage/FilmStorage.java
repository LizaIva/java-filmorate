package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {

    Film put(Film film);

    Film get(Integer id);

    List<Film> getAll();
}

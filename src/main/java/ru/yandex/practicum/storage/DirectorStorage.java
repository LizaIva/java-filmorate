package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.film.Director;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director getDirector(int id);

    List<Director> getAllDirector();

    Director updateDirector(Director director);

    int deleteDirector(int id);

    List<Director> getDirectorsByFilm(int id);

    void addFilmDirector(int filmId, List<Director> list);
}

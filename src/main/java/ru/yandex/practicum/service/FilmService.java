package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        userStorage.get(userId);
        Film film = filmStorage.get(filmId);
        film.addUserLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        userStorage.get(userId);
        Film film = filmStorage.get(filmId);
        film.removeUserLike(userId);
    }

    public List<Film> getTop(Integer limit) {
        List<Film> all = filmStorage.getAll();

        if (all.size() == 0) {
            return null;
        }

        return all.subList(0, limit > all.size() ? all.size() : limit);
    }
}

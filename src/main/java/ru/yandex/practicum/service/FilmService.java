package ru.yandex.practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.validation.FilmValidator;

import java.util.List;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public Film update(Film film) {
        FilmValidator.validateForUpdate(film);

        Film filmForUpdate = filmStorage.get(film.getId());

        log.info("Фильм найден, обновление фильма");

        if (film.getName() != null) {
            filmForUpdate.setName(film.getName());
        }

        if (film.getDescription() != null) {
            filmForUpdate.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null) {
            filmForUpdate.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null) {
            filmForUpdate.setDuration(film.getDuration());
        }

        return filmForUpdate;
    }

    public Film get(Integer id) {
        return filmStorage.get(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
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

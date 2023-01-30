package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.AlreadyExistException;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.storage.DirectorStorage;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.validation.FilmValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    public Film put(Film film) {
        deduplicateGenres(film);

        return filmStorage.put(film);
    }

    public Film update(Film film) {
        FilmValidator.validateForUpdate(film);
        deduplicateGenres(film);
        filmStorage.updateGenre(film.getId(), film.getGenres());

        return filmStorage.updateFilm(film);
    }

    private static void deduplicateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Genre> set = new HashSet<>(film.getGenres());
        film.setGenres(new ArrayList<>(set));
    }

    public Film get(Integer id) {
        return filmStorage.get(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.checkFilm(filmId);
        userStorage.checkUser(userId);
        filmStorage.addLike(filmId, userId);
        eventService.putEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.checkFilm(filmId);
        userStorage.checkUser(userId);
        filmStorage.deleteLike(filmId, userId);
        eventService.putEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public List<Film> getTop(Integer limit) {
        return filmStorage.getTop(limit);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public List<MPA> getAllCategories() {
        return filmStorage.getAllCategories();
    }

    public MPA getCategoryById(int categoryId) {
        return filmStorage.getCategoryById(categoryId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userStorage.checkUser(userId);
        userStorage.checkUser(friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public Film deleteById(int id) {
        filmStorage.checkFilm(id);

        return filmStorage.deleteById(id);
    }

    public List<Film> getFilmDirectorSortedBy(int directorId, String sortBy) {
        directorStorage.getDirector(directorId);

        if (sortBy.equals("year")) {
            return filmStorage.getFilmsDirectorSortedByYear(directorId);
        } else if (sortBy.equals("likes")) {
            return filmStorage.getFilmsDirectorSortedByLikes(directorId);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Film> findLimitPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        return filmStorage.findLimitPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public List<Film> findPopularFilmsByYearAndGenre(Integer year, Integer genreId) {
        return filmStorage.findPopularFilmsByYearAndGenre(year, genreId);
    }

    public List<Film> findPopularFilmsByYear(Integer year) {
        return filmStorage.findPopularFilmsByYear(year);
    }

    public List<Film> findPopularFilmsByGenre(Integer genreId) {
        return filmStorage.findPopularFilmsByGenre(genreId);
    }

    public List<Film> searchFilms(String query, String by) {
        query = query.toLowerCase();
        final String director = "director";
        final String title = "title";
        final String directorTitleVar1 = "director" + "," + "title";
        final String directorTitleVar2 = "title" + "," + "director";

        List<Film> allFilms = filmStorage.getAll();
        HashSet<Film> result = new HashSet<>();

        switch (by) {
            case (director):
                findFilmByDirector(allFilms, result, query);
                break;
            case (title):
                findFilmByTitle(allFilms, result, query);
                break;
            case (directorTitleVar1):
            case (directorTitleVar2):
                findFilmByTitle(allFilms, result, query);
                findFilmByDirector(allFilms, result, query);
                break;
        }

        return filmsSortedByLikes(new ArrayList<>(result));
    }

    public static List<Film> filmsSortedByLikes(List<Film> films) {
        films.sort(new LikesFilmReverseComparator());
        return films;
    }

    private void findFilmByDirector(List<Film> allFilms, HashSet<Film> result, String query) {
        for (Film film : allFilms) {
            for (Director dir : film.getDirectors()) {
                if (dir.getName().toLowerCase().contains(query)) {
                    result.add(film);
                    break;
                }
            }
        }
    }

    private void findFilmByTitle(List<Film> allFilms, HashSet<Film> result, String query) {
        for (Film film : allFilms) {
            if (film.getName().toLowerCase().contains(query)) {
                result.add(film);
            }
        }
    }
}

class LikesFilmReverseComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        return -1 * Integer.valueOf(film1.getUserLikes().size()).compareTo((film2.getUserLikes().size()));
    }
}
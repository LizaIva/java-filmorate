package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.storage.DirectorStorage;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;
import ru.yandex.practicum.validation.FilmValidator;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;

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
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.checkFilm(filmId);
        userStorage.checkUser(userId);
        filmStorage.deleteLike(filmId, userId);
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

    public List<Film> search(String query, String by) {
        return filmStorage.search(query, by);
    }

    public static List<Film> sortByLikes(List<Film> films) { //вывод списка лучших фильмов
        Collections.sort(films, new LikesFilmReverseComparator()); // отсортировали в обратном порядке по лайкам
        return films;
    }


}

class LikesFilmReverseComparator implements Comparator<Film> {// сортировка по лайкам в обратном порядке
    @Override
    public int compare(Film film1, Film film2) {
        return -1 * Integer.valueOf(film1.getUserLikes().size()).compareTo((film2.getUserLikes().size()));
    }
}


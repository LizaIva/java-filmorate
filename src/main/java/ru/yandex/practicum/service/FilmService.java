package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.storage.DirectorStorage;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.validation.FilmValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

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
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
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

    public List<Film> getFilmDirectorSortedBy(int directorId, String sortBy) {
        directorStorage.getDirector(directorId);
        if (sortBy.equals("year")){
            return filmStorage.getFilmsDirectorSortedByYear(directorId);
        } else if (sortBy.equals("likes")){
            return filmStorage.getFilmsDirectorSortedByLikes(directorId);
        } else {
            return new ArrayList<>();
        }
    }
}

package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;

import java.util.List;

public interface FilmStorage {

    Film put(Film film);

    Film get(Integer id);

    List<Film> getAll();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getTop(Integer limit);

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<MPA> getAllCategories();

    MPA getCategoryById(int categoryId);

    void updateGenre(int filmId, List<Genre> genreIds);

    Film updateFilm (Film film);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    public Film deleteById(int id);

    public void checkFilm(int id);
}

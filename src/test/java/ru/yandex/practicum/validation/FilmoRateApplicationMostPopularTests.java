package ru.yandex.practicum.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationMostPopularTests {
    private final FilmService filmService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from users");
        jdbcTemplate.update("delete from user_friends");
        jdbcTemplate.update("delete from film");
        jdbcTemplate.update("delete from film_likes");
        jdbcTemplate.update("delete from film_genre");
        jdbcTemplate.update("delete from reviews");
        jdbcTemplate.update("delete from event_feed");
        jdbcTemplate.update("DELETE FROM director");
        jdbcTemplate.update("DELETE FROM film_director");
    }

    @Test
    void findLimitPopularFilmsByGenreAndYearTest() {
        User user = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        film1.setGenres(List.of(filmService.getGenreById(2), filmService.getGenreById(6)));
        filmService.update(film1);

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1982, 10, 9), 120, filmService.getCategoryById(2)));
        film2.setGenres(List.of(filmService.getGenreById(4), filmService.getGenreById(6)));
        filmService.update(film2);

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        film3.setGenres(List.of(filmService.getGenreById(1)));
        filmService.update(film3);

        filmService.addLike(film2.getId(), user.getId(), 10);
        film2.setMiddleRating(10);

        List<Film> filmList = filmService.findLimitPopularFilmsByGenreAndYear(3, 4, 1982);

        assertAll(
                () -> assertEquals(film2, filmList.get(0), "Данные не верны"),
                () -> assertEquals(1, filmList.size(), "Данные не верны")
        );
    }

    @Test
    void findPopularFilmsByYearAndGenre() {
        User user = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        film1.setGenres(List.of(filmService.getGenreById(2), filmService.getGenreById(6)));
        filmService.update(film1);

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1982, 10, 9), 120, filmService.getCategoryById(2)));
        film2.setGenres(List.of(filmService.getGenreById(4), filmService.getGenreById(6)));
        filmService.update(film2);

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        film3.setGenres(List.of(filmService.getGenreById(1)));
        filmService.update(film3);

        filmService.addLike(film1.getId(), user.getId(), 10);
        film1.setMiddleRating(10);

        List<Film> filmList = filmService.findPopularFilmsByYearAndGenre(2005, 2);

        assertAll(
                () -> assertEquals(film1, filmList.get(0), "Данные не верны"),
                () -> assertEquals(1, filmList.size(), "Данные не верны")
        );
    }

    @Test
    void findPopularFilmsByYear() {
        User user = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        film1.setGenres(List.of(filmService.getGenreById(2), filmService.getGenreById(6)));
        filmService.update(film1);

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1982, 10, 9), 120, filmService.getCategoryById(2)));
        film2.setGenres(List.of(filmService.getGenreById(4), filmService.getGenreById(6)));
        filmService.update(film2);

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        film3.setGenres(List.of(filmService.getGenreById(1)));
        filmService.update(film3);

        filmService.addLike(film3.getId(), user.getId(), 10);
        film3.setMiddleRating(10);

        List<Film> filmList = filmService.findPopularFilmsByYear(2007);

        assertAll(
                () -> assertEquals(film3, filmList.get(0), "Данные не верны"),
                () -> assertEquals(1, filmList.size(), "Данные не верны")
        );
    }

    @Test
    void findPopularFilmsByGenre() {
        User user = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        film1.setGenres(List.of(filmService.getGenreById(2), filmService.getGenreById(6)));
        filmService.update(film1);

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1982, 10, 9), 120, filmService.getCategoryById(2)));
        film2.setGenres(List.of(filmService.getGenreById(4), filmService.getGenreById(6)));
        filmService.update(film2);

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        film3.setGenres(List.of(filmService.getGenreById(1)));
        filmService.update(film3);

        filmService.addLike(film1.getId(), user.getId(), 10);
        filmService.addLike(film2.getId(), user.getId(), 10);
        film1.setMiddleRating(10);
        film2.setMiddleRating(10);

        List<Film> filmList = filmService.findPopularFilmsByGenre(6);

        assertAll(
                () -> assertEquals(film1, filmList.get(0), "Данные не верны"),
                () -> assertEquals(film2, filmList.get(1), "Данные не верны"),
                () -> assertEquals(2, filmList.size(), "Данные не верны")
        );
    }
}
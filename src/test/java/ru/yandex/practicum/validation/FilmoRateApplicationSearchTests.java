package ru.yandex.practicum.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.*;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.DirectorService;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.ReviewService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationSearchTests {

    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;

    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from USERS");
        jdbcTemplate.update("delete from USER_FRIENDS");
        jdbcTemplate.update("delete from FILM");
        jdbcTemplate.update("delete from FILM_LIKES");
        jdbcTemplate.update("delete from FILM_GENRE");
        jdbcTemplate.update("delete from reviews");
    }



    @Test
    void SearchFilmsByDirectorTitleTest(){
        User userPut1 = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId1 = userPut1.getId();
        User userPut2 = userService.put(new User("alala1@test.t", "lalala1", "alalala", LocalDate.now()));
        int userId2 = userPut2.getId();

        Film film1 = (new Film("Бегущий по лезвию", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Director director1 = directorService.addDirector(new Director(1000, "Sprielbeg"));
        Director director2 = directorService.addDirector(new Director(1000, "Jonson"));
        List<Director> directors1 = Arrays.asList(director1, director2);
        film1.setDirectors(directors1);
        film1 = filmService.put(film1);
        int filmId1 = film1.getId();
        filmService.addLike(filmId1, userId1);
        film1 = filmService.get(filmId1);

        Film film2 = new Film("Бегущий по лезвию", "Фильм про будущее",
                LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2));
        director1 = directorService.addDirector(new Director(1000, "Jonson"));
        List<Director> directors2 = Arrays.asList(director1);
        film2.setDirectors(directors2);
        film2 = filmService.put(film2);
        int filmId2 = film2.getId();
        filmService.addLike(filmId2, userId1);
        filmService.addLike(filmId2, userId2);
        film2 = filmService.get(filmId2);

        Film film3 = new Film("Jonson", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3));
        film3 = filmService.put(film3);
        int filmId3 = film3.getId();
        film3 = filmService.get(filmId3);


        List<Film> allFilm = filmService.getAll();

        assertEquals(3, allFilm.size(), "Фильмы не были добавлены");
        assertEquals(true, allFilm.contains(filmService.get(filmId1)), "Фильм не был добавлен в список");
        assertEquals(true, allFilm.contains(filmService.get(filmId2)), "Фильм не был добавлен в список");
        assertEquals(true, allFilm.contains(filmService.get(filmId3)), "Фильм не был добавлен в список");


        List<Film> searchFilms1 = filmService.searchFilms("JonSon", "title,director");
        List<Film> searchFilms2 = filmService.searchFilms("notFound", "title,director");
        List<Film> searchFilms3 = filmService.searchFilms("Бегущий по лезвию", "title");
        List<Film> searchFilms4 = filmService.searchFilms("jonson", "director");

        List<Film> checkedSearchFilm1 = Arrays.asList(film2, film1, film3);
        List<Film> checkedSearchFilm2 = Arrays.asList();
        List<Film> checkedSearchFilm3 = Arrays.asList(film2, film1);
        List<Film> checkedSearchFilm4 = Arrays.asList(film2, film1);

        assertTrue(searchFilms1.equals(checkedSearchFilm1), "Неправильно выполнен поиск с запросом title,director");
        assertTrue(searchFilms2.equals(checkedSearchFilm2), "Неправильно выполнен поиск с запросом notFound");
        assertTrue(searchFilms3.equals(checkedSearchFilm3), "Неправильно выполнен поиск с запросом title");
        assertTrue(searchFilms4.equals(checkedSearchFilm4), "Неправильно выполнен поиск с запросом director");



    }






}

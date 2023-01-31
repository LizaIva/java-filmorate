package ru.yandex.practicum.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.DirectorService;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationDirectorTests {
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM film");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("delete from event_feed");
        jdbcTemplate.update("DELETE FROM director");
        jdbcTemplate.update("DELETE FROM film_director");
    }

    @Test
    @DisplayName("Добавление и получение режиссера")
    void putAndGetDirectorTest() {
        Director director = directorService.addDirector(new Director(1, "режиссер"));
        assertEquals(director, directorService.getDirector(director.getId()), "не верно получены данные о режиссере");
        assertEquals(Arrays.asList(director), directorService.getAllDirectors(),
                "неверное количество режиссеров в базе");
    }

    @Test
    @DisplayName("Обновление данных режиссера и получение режиссера")
    void updateAndGetDirectorTest() {
        Director director = directorService.addDirector(new Director(1, "режиссер"));
        director.setName("обновили");
        directorService.updateDirector(director);
        assertEquals(director, directorService.getDirector(director.getId()), "не верно получены данные о режиссере");
        assertEquals(Arrays.asList(director), directorService.getAllDirectors(),
                "неверное количество режиссеров в базе");
    }

    @Test
    @DisplayName("Удаление режиссера")
    void deleteDirectorTest() {
        assertEquals(new ArrayList<>(), directorService.getAllDirectors(), "Список не пуст");
        Director director = directorService.addDirector(new Director(1, "режиссер"));
        assertEquals(Arrays.asList(director), directorService.getAllDirectors(),
                "неверное количество режиссеров в базе");
        directorService.deleteDirector(director.getId());
        assertEquals(new ArrayList<>(), directorService.getAllDirectors(), "Режиссер не удален");
    }

    @Test
    @DisplayName("получение, обновление, удаление режиссера с неверным id")
    void getUpdateDeleteDirectorByWrongIdTest() {
        assertThrows(UnknownDataException.class, () -> directorService.getDirector(666));
        assertThrows(UnknownDataException.class, () -> directorService.updateDirector(
                new Director(666, "666")));
        assertThrows(UnknownDataException.class, () -> directorService.deleteDirector(666));
    }

    @Test
    @DisplayName("Вывод всех фильмов режиссёра, отсортированных по количеству лайков")
    void SearchFilmsByDirectorSortedLikesTest() {
        User userPut1 = userService.put(
                new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId1 = userPut1.getId();

        Film film1 = (new Film("Бегущий по лезвию", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Film film2 = (new Film("Бегущий по второму лезвию", "поставили треш на поток",
                LocalDate.of(2006, 10, 9), 100, filmService.getCategoryById(1)));
        Film film3 = (new Film("Бегущий по третему лезвию", "поставили треш на поток",
                LocalDate.of(2007, 10, 9), 100, filmService.getCategoryById(1)));

        Director director1 = directorService.addDirector(new Director(1000, "Sprielbeg"));
        Director director2 = directorService.addDirector(new Director(1000, "Jonson"));

        film1.setDirectors(Arrays.asList(director1));
        film1 = filmService.put(film1);

        filmService.addLike(film1.getId(), userId1);

        film2.setDirectors(Arrays.asList(director1));
        film2 = filmService.put(film2);

        film3.setDirectors(Arrays.asList(director2));

        List<Film> sortedListFilm = Arrays.asList(filmService.get(film1.getId()), filmService.get(film2.getId()));

        assertEquals(sortedListFilm, filmService.getFilmDirectorSortedBy(director1.getId(), "likes"),
                "Неправильная сортировка по лайкам");
    }

    @Test
    @DisplayName("Вывод всех фильмов режиссёра, отсортированных по годам")
    void SearchFilmsByDirectorSortedYearTest() {
        Film film1 = (new Film("Бегущий по лезвию", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Film film2 = (new Film("Бегущий по второму лезвию", "поставили треш на поток",
                LocalDate.of(2006, 10, 9), 100, filmService.getCategoryById(1)));
        Film film3 = (new Film("Бегущий по третему лезвию", "поставили треш на поток",
                LocalDate.of(2007, 10, 9), 100, filmService.getCategoryById(1)));

        Director director1 = directorService.addDirector(new Director(1000, "Sprielbeg"));
        Director director2 = directorService.addDirector(new Director(1000, "Jonson"));

        film1.setDirectors(Arrays.asList(director1));
        film1 = filmService.put(film1);

        film2.setDirectors(Arrays.asList(director1));
        film2 = filmService.put(film2);

        film3.setDirectors(Arrays.asList(director2));

        List<Film> sortedListFilm = Arrays.asList(filmService.get(film1.getId()), filmService.get(film2.getId()));

        assertEquals(sortedListFilm, filmService.getFilmDirectorSortedBy(director1.getId(), "year"),
                "Неправильная сортировка по году");
    }

    @Test
    @DisplayName("Запрос всех фильмов режиссёра, по неверному id")
    void SearchFilmsByDirectorSortedWrongIdTest() {
        assertThrows(UnknownDataException.class, () -> filmService.getFilmDirectorSortedBy(666, "year"));
        assertThrows(UnknownDataException.class, () -> filmService.getFilmDirectorSortedBy(666, "likes"));
    }


}

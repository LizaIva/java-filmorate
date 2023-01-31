package ru.yandex.practicum.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;
import ru.yandex.practicum.model.film.*;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserService userService;
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;
    private final ReviewService reviewService;
    private final EventService eventService;

    private final DirectorService directorService;

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
    public void putAndGetUserTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);
        assertEquals(userPut.getName(), actualUser.getName(), "Неверное сохранение имени пользователя в БД");
        assertEquals(userPut.getLogin(), actualUser.getLogin(), "Неверно сохранен login пользователя в БД");
        assertEquals(userPut.getEmail(), actualUser.getEmail(), "Неверно сохранена почта пользователя в БД");
        assertEquals(userPut.getBirthday(), actualUser.getBirthday(), "Неверно сохранена дата рождения пользователя в БД");
    }

    @Test
    void shouldPutReview() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Review postReview = reviewService.postReview(new Review("asas", false, userId, filmId));
        Review review = reviewService.getReview(postReview.getReviewId());

        assertEquals(postReview.getReviewId(), review.getReviewId());
        assertEquals(postReview.getContent(), review.getContent());
        assertEquals(postReview.getFilmId(), review.getFilmId());
    }

    @Test
    void shouldThrowErrorAndGetReviewById() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Review postReview = reviewService.postReview(new Review("asas", false, userId, filmId));

        assertEquals(postReview.getReviewId(), reviewService.getReview(postReview.getReviewId()).getReviewId());
        assertThrows(UnknownDataException.class, () -> reviewService.getReview(1000));
    }

    @Test
    void wontPostReviewWithoutUser() {
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Review postReview = new Review("asas", false, 1, putFilm.getId());

        assertThrows(UnknownDataException.class, () -> reviewService.postReview(postReview));
    }

    @Test
    void wontPostReviewWithoutFilm() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        Review postReview = new Review("asas", false, userPut.getId(), 1);

        assertThrows(UnknownDataException.class, () -> reviewService.postReview(postReview));
    }

    @Test
    void shouldDeleteReview() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Review postReview = reviewService.postReview(new Review("asas", false, userId, filmId));
        reviewService.deleteReview(postReview.getReviewId());

        assertThrows(UnknownDataException.class, () -> reviewService.getReview(postReview.getReviewId()));
    }

    @Test
    public void putUserWithoutNameTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);
        assertEquals(userPut.getLogin(), actualUser.getName(), "Не произошло замены пустого имени логином пользователя");
    }

    @Test
    public void updateUserTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.now()));
        int userId = userPut.getId();

        userPut.setLogin("work");
        userService.update(userPut);

        userPut.setEmail("hello@test.t");
        userService.update(userPut);

        userPut.setName("");
        userService.update(userPut);

        userPut.setBirthday(LocalDate.of(2007, 7, 9));
        userService.update(userPut);

        User actualUser = userService.get(userId);
        assertNotEquals("lalala", actualUser.getLogin(), "Не произошла замена логина");
        assertNotEquals("alala@test.t", actualUser.getEmail(), "Не произошла замена почты");
        assertNotEquals("Liza", actualUser.getName(), "Не произошла замена имени");
        assertEquals(actualUser.getLogin(), actualUser.getName(), "Не произошло замены пустого имени логином пользователя");
        assertNotEquals(LocalDate.now(), actualUser.getBirthday(), "Не произошла замена даты рождения");
    }

    @Test
    void getUserBuIdAndWithWrongIdTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);

        assertEquals(userPut, actualUser, "Пользователь по id не найден");
        assertThrows(UnknownDataException.class, () -> userService.get(877869));
    }

    @Test
    void getAllUsersTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick",
                LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        List<User> allUsers = userService.getAll();

        assertEquals(3, allUsers.size(), "Пользователи не были добавлены");
        assertTrue(allUsers.contains(userService.get(userId1)), "Пользователь не был добавлен в список");
        assertTrue(allUsers.contains(userService.get(userId2)), "Пользователь не был добавлен в список");
        assertTrue(allUsers.contains(userService.get(userId3)), "Пользователь не был добавлен в список");
    }

    @Test
    void getUsersByIds() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick",
                LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        List<User> usersByIds = userService.getUsersByIds(List.of(userId1, userId2));

        assertEquals(2, usersByIds.size(), "Пользователи не были добавлены");
        assertTrue(usersByIds.contains(userService.get(userId1)), "Пользователь не был добавлен в список");
        assertTrue(usersByIds.contains(userService.get(userId2)), "Пользователь не был добавлен в список");
        assertFalse(usersByIds.contains(userService.get(userId3)), "Пользователь был добавлен в список");
    }

    @Test
    void addFriendAndAcceptFriendshipTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick",
                LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        userService.addFriends(userId1, userId2);

        User actualUser1 = userService.get(userId1);
        User actualUser2 = userService.get(userId2);

        assertEquals(1, actualUser1.getFriends().size(), "Заявка на дружбу не была отправлена");
        assertEquals(0, actualUser2.getFriends().size(), "Произошло подтверждение дружбы");

        userService.acceptFriendship(userId2, userId1);

        actualUser1 = userService.get(userId1);
        actualUser2 = userService.get(userId2);

        assertEquals(1, actualUser1.getFriends().size(), "Заявка на дружбу не была отправлена");
        assertEquals(1, actualUser2.getFriends().size(), "Не произошло подтверждение дружбы");

        assertThrows(UnknownDataException.class, () -> userService.acceptFriendship(userId2, userId3));

        List<Event> events = eventService.getEvents(userId1);
        assertEquals(2, events.size(), "Эвенты не были добавлены");
    }

    @Test
    void removeFriendTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        userService.addFriends(userId1, userId2);
        userService.acceptFriendship(userId2, userId1);

        User actualUser1 = userService.get(userId1);
        User actualUser2 = userService.get(userId2);

        assertEquals(1, actualUser1.getFriends().size(), "Не произошло добавления в друзья");
        assertEquals(1, actualUser2.getFriends().size(), "Не произошло подтверждение дружбы");

        userService.removeFriends(userId1, userId2);

        actualUser1 = userService.get(userId1);
        actualUser2 = userService.get(userId2);

        assertEquals(0, actualUser1.getFriends().size(), "Не произошло удаления из друзей");
        assertEquals(0, actualUser2.getFriends().size(), "Не произошло удаления из друзей");

        List<Event> events = eventService.getEvents(userId1);
        assertEquals(2, events.size(), "Эвенты не были добавлены");

        List<Event> events2 = eventService.getEvents(userId2);
        assertEquals(1, events2.size(), "Эвенты не были добавлены");
    }

    @Test
    void foundCommonFriendsTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick",
                LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        userService.addFriends(userId1, userId2);
        userService.acceptFriendship(userId2, userId1);

        userService.addFriends(userId2, userId3);
        userService.acceptFriendship(userId3, userId2);

        User actualUser1 = userService.get(userId1);
        User actualUser2 = userService.get(userId2);
        User actualUser3 = userService.get(userId3);

        List<User> commonFriends = userService.commonFriends(actualUser1.getId(), actualUser3.getId());

        assertEquals(1, commonFriends.size(), "Нет общих друзей");
        assertTrue(commonFriends.contains(actualUser2), "Неверный общий друг");
    }

    @Test
    void foundUsersFriendsTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name",
                LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick",
                LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        userService.addFriends(userId1, userId2);
        userService.addFriends(userId1, userId3);

        User actualUser1 = userService.get(userId1);
        List<User> usersFriend = userService.getAllFriends(actualUser1.getId());
        assertEquals(2, usersFriend.size(), "Не произошло добавления в друзья");

        userService.acceptFriendship(userId2, userId1);
        userService.acceptFriendship(userId3, userId1);
        actualUser1 = userService.get(userId1);
        usersFriend = userService.getAllFriends(actualUser1.getId());
        assertEquals(2, usersFriend.size(), "Не произошло добавления в друзья");

        List<Event> events = eventService.getEvents(userId1);
        assertEquals(4, events.size(), "Эвенты не были добавлены");
    }

    @Test
    void getStatusName() {
        String nameAccept = userService.getStatusName(0);
        String nameNotAccept = userService.getStatusName(1);

        assertEquals("accepted", nameAccept, "Неверное имя статуса");
        assertEquals("not accepted", nameNotAccept, "Неверное имя статуса");
    }

    @Test
    void deleteTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza",
                LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();
        userService.deleteById(userId1);
        assertEquals(new ArrayList<User>(), userService.getAll());
    }

    @Test
    void putFilmTest() {
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Film actualFilm = filmService.get(filmId);
        assertEquals(putFilm.getName(), actualFilm.getName(), "Произошло неверное сохранение названия фильма");
        assertEquals(putFilm.getDescription(), actualFilm.getDescription(), "Произошло неверное сохранение описания фильма");
        assertEquals(putFilm.getReleaseDate(), actualFilm.getReleaseDate(), "Произошло неверное сохранение даты релиза фильма");
        assertEquals(putFilm.getDuration(), actualFilm.getDuration(), "Произошло неверное сохранение продолжительности фильма");
        assertEquals(putFilm.getMpa(), actualFilm.getMpa(), "Произошло неверное сохранение категории фильма");
    }

    @Test
    void getFilmWithWrongId() {
        assertThrows(UnknownDataException.class, () -> filmService.get(123));
    }

    @Test
    public void deleteFilmTest() {
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.deleteById(filmId);
        assertEquals(new ArrayList<>(), filmService.getAll());
    }

    @Test
    void updateFilmWithGenreTest() {
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        putFilm.setName("ВВТ");
        filmService.update(putFilm);

        putFilm.setDescription("CGLL");
        filmService.update(putFilm);

        putFilm.setReleaseDate(LocalDate.of(2008, 8, 9));
        filmService.update(putFilm);

        putFilm.setDuration(120);
        filmService.update(putFilm);

        putFilm.setMpa(filmService.getCategoryById(3));
        filmService.update(putFilm);

        putFilm.setGenres(List.of(filmService.getGenreById(1), filmService.getGenreById(3)));
        filmService.update(putFilm);

        Film actualFilm = filmService.get(filmId);

        assertEquals(putFilm.getName(), actualFilm.getName(), "Произошло неверное обновление названия фильма");
        assertEquals(putFilm.getDescription(), actualFilm.getDescription(), "Произошло неверное обновление описания фильма");
        assertEquals(putFilm.getReleaseDate(), actualFilm.getReleaseDate(), "Произошло неверное обновление даты релиза фильма");
        assertEquals(putFilm.getDuration(), actualFilm.getDuration(), "Произошло неверное обновление продолжительности фильма");
        assertEquals(putFilm.getMpa(), actualFilm.getMpa(), "Произошло неверное обновление категории фильма");
        assertEquals(putFilm.getGenres(), actualFilm.getGenres(), "Произошло неверное обновление жанров фильма");
    }

    @Test
    void getAllFilmsTest() {
        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId1 = film1.getId();

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее",
                LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2)));
        int filmId2 = film2.getId();

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц",
                LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        int filmId3 = film3.getId();

        List<Film> allFilm = filmService.getAll();

        assertEquals(3, allFilm.size(), "Фильмы не были добавлены");
        assertTrue(allFilm.contains(filmService.get(filmId1)), "Фильм не был добавлен в список");
        assertTrue(allFilm.contains(filmService.get(filmId2)), "Фильм не был добавлен в список");
        assertTrue(allFilm.contains(filmService.get(filmId3)), "Фильм не был добавлен в список");
    }

    @Test
    void addLikeTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.addLike(filmId, userId);
        Film actualFilm = filmService.get(filmId);
        assertEquals(1, actualFilm.getUserLikes().size(), "Лайк не был поставлен");

        Film actualFilm2 = filmService.get(filmId);
        assertEquals(1, actualFilm2.getUserLikes().size(), "Один пользователь поставил 2 лайка на один фильм");

        assertThrows(UnknownDataException.class, () -> filmService.addLike(filmId, 123));
        assertThrows(UnknownDataException.class, () -> filmService.addLike(145, userId));

        List<Event> events = eventService.getEvents(userId);
        assertEquals(1, events.size(), "Эвент не был добавлен");
    }

    @Test
    void deleteLikeTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.addLike(filmId, userId);
        Film actualFilm = filmService.get(filmId);
        assertEquals(1, actualFilm.getUserLikes().size(), "Лайк не был поставлен");


        filmService.removeLike(filmId, userId);
        Film actualFilm2 = filmService.get(filmId);
        assertEquals(0, actualFilm2.getUserLikes().size(), "Лайк не был удален");


        List<Event> events = eventService.getEvents(userId);
        assertEquals(2, events.size(), "Эвенты не были добавлены");
    }

    @Test
    void getTopTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        User user2 = userService.put(new User("jlj@test.t", "Hello", "Bin", LocalDate.now()));
        int userId2 = user2.getId();

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId1 = film1.getId();

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее",
                LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2)));
        int filmId2 = film2.getId();

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц",
                LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        int filmId3 = film3.getId();

        filmService.addLike(filmId1, userId);
        filmService.addLike(filmId3, userId);
        filmService.addLike(filmId3, userId2);

        Film actualFilm1 = filmService.get(filmId1);
        Film actualFilm2 = filmService.get(filmId2);
        Film actualFilm3 = filmService.get(filmId3);

        List<Film> top = filmService.getTop(3);
        assertEquals(3, top.size(), "Фильмы не были добавлены в список");
        assertEquals(actualFilm3, top.get(0), "Не верный порядок вывода фильмов");
        assertEquals(actualFilm1, top.get(1), "Не верный порядок вывода фильмов");
        assertEquals(actualFilm2, top.get(2), "Не верный порядок вывода фильмов");
    }

    @Test
    void getAllGenresAndByIdTest() {
        List<Genre> genres = filmService.getAllGenres();
        assertEquals(6, genres.size(), "Не все жанры добавлены в список");

        Genre genre1 = filmService.getGenreById(1);
        assertEquals(genres.get(0), genre1, "Поиск жанра по id не работает");

        assertThrows(UnknownDataException.class, () -> filmService.getGenreById(10));
    }

    @Test
    void getAllMpaAndByIdTest() {
        List<MPA> mpas = filmService.getAllCategories();
        assertEquals(5, mpas.size(), "Не все категории добавлены в список");

        MPA mpa1 = filmService.getCategoryById(1);
        assertEquals(mpas.get(0), mpa1, "Поиск категории по id не работает");

        assertThrows(UnknownDataException.class, () -> filmService.getCategoryById(10));
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

        filmService.addLike(film2.getId(), user.getId());

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

        filmService.addLike(film1.getId(), user.getId());

        List<Film> filmList = filmService.findPopularFilmsByYearAndGenre(2005, 2);

        assertAll(
                () -> assertEquals(film1, filmList.get(0), "Данные не верны"),
                () -> assertEquals(1, filmList.size(), "Данные не верны")
        );
    }

    void putAndGetEventTest() {
        User putUser = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = putUser.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Event event = new Event(1, 123344556L, userId, EventType.LIKE, Operation.ADD, filmId);
        eventService.putEvent(event.getUserId(), event.getEventType(), event.getOperation(), event.getEntityId());

        List<Event> eventsUser = eventService.getEvents(userId);
        Event actualEvent = eventsUser.get(0);

        assertEquals(1, eventsUser.size(), "Событие не было добавлено.");
        assertEquals(actualEvent.getEventType(), event.getEventType(), "Произошло неверное сохранение данных эвента.");
        assertEquals(actualEvent.getUserId(), event.getUserId(), "Произошло неверное сохранение данных эвента.");
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

        filmService.addLike(film3.getId(), user.getId());

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

        filmService.addLike(film1.getId(), user.getId());
        filmService.addLike(film2.getId(), user.getId());

        List<Film> filmList = filmService.findPopularFilmsByGenre(6);

        assertAll(
                () -> assertEquals(film1, filmList.get(0), "Данные не верны"),
                () -> assertEquals(film2, filmList.get(1), "Данные не верны"),
                () -> assertEquals(2, filmList.size(), "Данные не верны")
        );
    }

    @Test
    @DisplayName("Получение списка общих фильмов для двух пользователей")
    void getCommonFilmsTest() {
        Film film = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));

        User user1 = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));
        User user2 = userService.put(new User("friend@mail.ru", "friend", "friend adipisicing", LocalDate.now()));

        filmService.addLike(film.getId(), user1.getId());
        filmService.addLike(film.getId(), user2.getId());

        List<Film> actualResult = filmService.getCommonFilms(user1.getId(), user2.getId());
        assertAll(
                () -> assertThat(actualResult.size())
                        .as("Длина списка общих фильмов не соответствует ожидаемой!")
                        .isOne(),
                () -> assertThat(actualResult.get(0))
                        .as("Список общих фильмов не соответствует ожидаемому!")
                        .usingRecursiveComparison()
                        .ignoringFields("genres", "userLikes")
                        .isEqualTo(film)
        );
    }

    @Test
    @DisplayName("Получение списка общих фильмов для двух пользователей, у которых их нет")
    void getNoCommonFilmsTest() {
        Film film = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));

        User user1 = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));
        User user2 = userService.put(new User("friend@mail.ru", "friend", "friend adipisicing", LocalDate.now()));

        filmService.addLike(film.getId(), user1.getId());

        List<Film> actualResult = filmService.getCommonFilms(user1.getId(), user2.getId());
        assertThat(actualResult.size())
                .as("Длина списка общих фильмов не соответствует ожидаемой!")
                .isZero();
    }

    @Test
    @DisplayName("Получение списка общих фильмов для двух пользователей в случае, если один из них не существут")
    void getCommonFilmsForUndefinedUser() {
        User user1 = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));

        assertThatExceptionOfType(UnknownDataException.class)
                .isThrownBy(() -> filmService.getCommonFilms(user1.getId(), user1.getId() + 1))
                .withMessage("user с id = %s не найден.", user1.getId() + 1);
    }

    @Test
    @DisplayName("Получение рекомендаций для пользователя")
    void getRecommendationsTest() {
        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее",
                LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2)));

        User user1 = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));
        User user2 = userService.put(new User("friend@mail.ru", "friend", "adipisicing", LocalDate.now()));

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> actualResult = userService.getRecommendations(user2.getId());
        assertAll(
                () -> assertThat(actualResult.size())
                        .as("Длина списка рекомендаций не соответствует ожидаемой!")
                        .isEqualTo(1),
                () -> assertThat(actualResult.get(0))
                        .as("Список рекомендаций не соответствует ожидаемому!")
                        .usingRecursiveComparison()
                        .ignoringFields("genres", "userLikes")
                        .isEqualTo(film2)
        );
    }

    @Test
    @DisplayName("Получение рекомендаций для несуществующего пользователя")
    void getRecommendationsForUndefinedUserTest() {
        int id = new Random().nextInt(100);

        assertThatExceptionOfType(UnknownDataException.class)
                .isThrownBy(() -> userService.getRecommendations(id))
                .withMessage("user с id = %s не найден.", id);
    }

    @Test
    @DisplayName("Получение рекомендаций в случае отсутствия лайков у пользователя")
    void getRecommendationsIfNoLikesTest() {
        User user = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));

        assertThat(userService.getRecommendations(user.getId()).size())
                .as("Длина списка рекомендаций не соответствует ожидаемой!")
                .isZero();
    }

    @Test
    @DisplayName("Получение рекомендаций в случае, если у наиболее похожего пользователя такой же список лайков")
    void getRecommendationsIfSameLikesTest() {
        Film film = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей",
                LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));

        User user1 = userService.put(new User("mail@mail.ru", "dolore", "Nick Name", LocalDate.now()));
        User user2 = userService.put(new User("friend@mail.ru", "friend", "friend", LocalDate.now()));
        List<Integer> userIds = Arrays.asList(user1.getId(), user2.getId());

        userIds.forEach(userId -> filmService.addLike(film.getId(), userId));

        List<Film> actualResult = userService.getRecommendations(userIds.get(new Random().nextInt(userIds.size())));
        assertThat(actualResult.size())
                .as("Длина списка рекомендаций не соответствует ожидаемой!")
                .isZero();
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
        assertThrows(UnknownDataException.class, () -> filmService.getFilmDirectorSortedBy(666 ,"year"));
        assertThrows(UnknownDataException.class, () -> filmService.getFilmDirectorSortedBy(666 ,"likes"));
    }
}

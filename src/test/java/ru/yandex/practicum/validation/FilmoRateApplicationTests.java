package ru.yandex.practicum.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.AlreadyExistException;
import ru.yandex.practicum.exception.UnknownDataException;


import ru.yandex.practicum.model.event.Event;
import ru.yandex.practicum.model.event.constants.EventType;
import ru.yandex.practicum.model.event.constants.Operation;

import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.EventService;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.ReviewService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    private final UserService userService;
    private final FilmService filmService;

    private final JdbcTemplate jdbcTemplate;

    private final ReviewService reviewService;

    private final EventService eventService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from USERS");
        jdbcTemplate.update("delete from USER_FRIENDS");
        jdbcTemplate.update("delete from FILM");
        jdbcTemplate.update("delete from FILM_LIKES");
        jdbcTemplate.update("delete from FILM_GENRE");
        jdbcTemplate.update("delete from reviews");
        jdbcTemplate.update("delete from EVENT_FEED");
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
    void shouldPutReview(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();
        Review postReview = reviewService.postReview(new Review("asas",false, userId,filmId));
        Review review = reviewService.getReview(postReview.getReviewId());

        assertEquals(postReview.getReviewId(), review.getReviewId());
        assertEquals(postReview.getContent(), review.getContent());
        assertEquals(postReview.getFilmId(),review.getFilmId());
    }

    @Test
    void shouldThrowErrorAndGetReviewById(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();
        Review postReview = reviewService.postReview(new Review("asas",false, userId,filmId));

        assertEquals(postReview.getReviewId(), reviewService.getReview(postReview.getReviewId()).getReviewId());
        assertThrows(UnknownDataException.class, () -> reviewService.getReview(1000));
    }

    @Test
    void wontPostReviewWithoutUser() {
        filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        Review postReview = new Review("asas",false, 1,1);
        assertThrows(UnknownDataException.class, () -> reviewService.postReview(postReview));
    }

    @Test
    void wontPostReviewWithoutFilm() {
        userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        Review postReview = new Review("asas",false, 1,1);

        assertThrows(UnknownDataException.class,() -> reviewService.postReview(postReview));
    }

    @Test
    void shouldDeleteReview() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();
        Review postReview = reviewService.postReview(new Review("asas",false, userId,filmId));

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
        assertNotEquals("Liza", actualUser.getName(), "Не произошла замена имени" );
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
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick", LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        List<User> allUsers = userService.getAll();

        assertEquals(3, allUsers.size(), "Пользователи не были добавлены");
        assertEquals(true, allUsers.contains(userService.get(userId1)), "Пользователь не был добавлен в список");
        assertEquals(true, allUsers.contains(userService.get(userId2)), "Пользователь не был добавлен в список");
        assertEquals(true, allUsers.contains(userService.get(userId3)), "Пользователь не был добавлен в список");
    }

    @Test
    void getUsersByIds() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick", LocalDate.of(1987, 11, 17)));
        int userId3 = user3.getId();

        List<User> usersByIds = userService.getUsersByIds(List.of(userId1, userId2));

        assertEquals(2, usersByIds.size(), "Пользователи не были добавлены");
        assertEquals(true, usersByIds.contains(userService.get(userId1)), "Пользователь не был добавлен в список");
        assertEquals(true, usersByIds.contains(userService.get(userId2)), "Пользователь не был добавлен в список");
        assertEquals(false, usersByIds.contains(userService.get(userId3)), "Пользователь был добавлен в список");
    }

    @Test
    void addFriendAndAcceptFriendshipTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick", LocalDate.of(1987, 11, 17)));
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
    void removeFriendTest(){
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
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
    void foundCommonFriendsTest(){
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick", LocalDate.of(1987, 11, 17)));
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
        assertEquals(true, commonFriends.contains(actualUser2), "Неверный общий друг");
    }

    @Test
    void foundUsersFriendsTest(){
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();

        User user2 = userService.put(new User("khfk@mail.ru", "dada", "Name", LocalDate.of(1998, 7, 9)));
        int userId2 = user2.getId();

        User user3 = userService.put(new User("lioh@mail.ru", "kaka", "Nick", LocalDate.of(1987, 11, 17)));
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
        assertEquals( 2, usersFriend.size(), "Не произошло добавления в друзья");

        List<Event> events = eventService.getEvents(userId1);
        assertEquals(4, events.size(), "Эвенты не были добавлены");
    }

    @Test
    void getStatusName(){
        String nameAccept = userService.getStatusName(0);
        String nameNotAccept = userService.getStatusName(1);
        assertEquals("accepted" , nameAccept, "Неверное имя статуса");
        assertEquals("not accepted" , nameNotAccept, "Неверное имя статуса");
    }

    @Test
    void deleteTest() {
        User user1 = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.of(2002, 10, 7)));
        int userId1 = user1.getId();
        userService.deleteById(userId1);
        assertEquals(new ArrayList<User>(), userService.getAll());
    }

    @Test
    void putFilmTest(){
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Film actualFilm = filmService.get(filmId);
        assertEquals(putFilm.getName(), actualFilm.getName(), "Произошло неверное сохранение названия фильма");
        assertEquals(putFilm.getDescription(), actualFilm.getDescription(), "Произошло неверное сохранение описания фильма");
        assertEquals(putFilm.getReleaseDate(), actualFilm.getReleaseDate(), "Произошло неверное сохранение даты релиза фильма");
        assertEquals(putFilm.getDuration(), actualFilm.getDuration(), "Произошло неверное сохранение продолжительности фильма");
        assertEquals(putFilm.getMpa(), actualFilm.getMpa(), "Произошло неверное сохранение категории фильма");
    }

    @Test
    void getFilmWithWrongId(){
        assertThrows(UnknownDataException.class, () -> filmService.get(123));
    }

    @Test
    public void deleteFilmTest() {
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.deleteById(filmId);
        assertEquals(new ArrayList<>(), filmService.getAll());
    }

    @Test
    void updateFilmWithGenreTest(){
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        putFilm.setName("ВВТ");
        filmService.update(putFilm);

        putFilm.setDescription("CGLL");
        filmService.update(putFilm);

        putFilm.setReleaseDate(LocalDate.of(2008, 8,9));
        filmService.update(putFilm);

        putFilm.setDuration(120);
        filmService.update(putFilm);

        putFilm.setMpa(filmService.getCategoryById(3));
        filmService.update(putFilm);

        putFilm.setGenres(List.of(filmService.getGenreById(1), filmService.getGenreById(3)));
        filmService.update(putFilm);

        Film actualFilm = filmService.get(filmId);

        assertEquals(putFilm.getName(), actualFilm.getName(), "Произошло неверное обновление названия фильма");
        assertEquals( putFilm.getDescription(), actualFilm.getDescription(), "Произошло неверное обновление описания фильма");
        assertEquals(putFilm.getReleaseDate(), actualFilm.getReleaseDate(), "Произошло неверное обновление даты релиза фильма");
        assertEquals(putFilm.getDuration(), actualFilm.getDuration(), "Произошло неверное обновление продолжительности фильма");
        assertEquals(putFilm.getMpa(), actualFilm.getMpa(), "Произошло неверное обновление категории фильма");
        assertEquals(putFilm.getGenres(), actualFilm.getGenres(), "Произошло неверное обновление жанров фильма");
    }

    @Test
    void getAllFilmsTest(){
        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId1 = film1.getId();

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2)));
        int filmId2 = film2.getId();

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
        int filmId3 = film3.getId();

        List<Film> allFilm = filmService.getAll();

        assertEquals(3, allFilm.size(), "Фильмы не были добавлены");
        assertEquals(true, allFilm.contains(filmService.get(filmId1)), "Фильм не был добавлен в список");
        assertEquals(true, allFilm.contains(filmService.get(filmId2)), "Фильм не был добавлен в список");
        assertEquals(true, allFilm.contains(filmService.get(filmId3)), "Фильм не был добавлен в список");
    }

    @Test
    void addLikeTest(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.addLike(filmId, userId);
        Film actualFilm = filmService.get(filmId);
        assertEquals(1, actualFilm.getUserLikes().size(), "Лайк не был поставлен");

        assertThrows(AlreadyExistException.class, () -> filmService.addLike(filmId, userId));
        Film actualFilm2 = filmService.get(filmId);
        assertEquals(1, actualFilm2.getUserLikes().size(), "Один пользователь поставил 2 лайка на один фильм");

        assertThrows(UnknownDataException.class, () -> filmService.addLike(filmId, 123));
        assertThrows(UnknownDataException.class, () -> filmService.addLike(145, userId));

        List<Event> events = eventService.getEvents(userId);
        assertEquals(1, events.size(), "Эвент не был добавлен");

    }

    @Test
    void deleteLikeTest(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        User user2 = userService.put(new User("jlj@test.t", "Hello", "Bin", LocalDate.now()));
        int userId2 = user2.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.addLike(filmId, userId);
        Film actualFilm = filmService.get(filmId);
        assertEquals(1, actualFilm.getUserLikes().size(), "Лайк не был поставлен");

        assertThrows(AlreadyExistException.class, () -> filmService.removeLike(filmId, userId2));
        Film actualFilm1 = filmService.get(filmId);
        assertEquals(1, actualFilm1.getUserLikes().size(), "Произошло удаление лайка от пользователя, который этот лайк не ставил");

        filmService.removeLike(filmId, userId);
        Film actualFilm2 = filmService.get(filmId);
        assertEquals(0, actualFilm2.getUserLikes().size(), "Лайк не был удален");

        assertThrows(AlreadyExistException.class, () -> filmService.removeLike(filmId, userId));
        Film actualFilm3 = filmService.get(filmId);
        assertNotEquals(-1, actualFilm3.getUserLikes().size(), "Произошло удаление несуществующего лайка");

        List<Event> events = eventService.getEvents(userId);
        assertEquals(2, events.size(), "Эвенты не были добавлены");
    }

    @Test
    void getTopTest(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        User user2 = userService.put(new User("jlj@test.t", "Hello", "Bin", LocalDate.now()));
        int userId2 = user2.getId();

        Film film1 = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId1 = film1.getId();

        Film film2 = filmService.put(new Film("Бегущий по лезвию", "Фильм про будущее", LocalDate.of(1998, 10, 9), 120, filmService.getCategoryById(2)));
        int filmId2 = film2.getId();

        Film film3 = filmService.put(new Film("Сплетница", "Сериал про сплетниц", LocalDate.of(2007, 10, 9), 45, filmService.getCategoryById(3)));
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
    void getAllGenresAndByIdTest(){
        List<Genre> genres= filmService.getAllGenres();
        assertEquals(6, genres.size(), "Не все жанры добавлены в список");

        Genre genre1 = filmService.getGenreById(1);
        assertEquals(genres.get(0), genre1, "Поиск жанра по id не работает");

        assertThrows(UnknownDataException.class, () -> filmService.getGenreById(10));
    }

    @Test
    void getAllMpaAndByIdTest(){
        List<MPA> mpas= filmService.getAllCategories();
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

    void putAndGetEventTest(){
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
}

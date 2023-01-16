package ru.yandex.practicum.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    private final UserService userService;
    private final FilmService filmService;

    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from USERS");
        jdbcTemplate.update("delete from USER_FRIENDS");
        jdbcTemplate.update("delete from FILM");
        jdbcTemplate.update("delete from FILM_LIKES");
        jdbcTemplate.update("delete from FILM_GENRE");
    }


    @Test
    public void putAndGetUserTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);
        assertEquals("Неверное сохранение имени пользователя в БД", userPut.getName(), actualUser.getName());
        assertEquals("Неверно сохранен login пользователя в БД", userPut.getLogin(), actualUser.getLogin());
        assertEquals("Неверно сохранена почта пользователя в БД", userPut.getEmail(), actualUser.getEmail());
        assertEquals("Неверно сохранена дата рождения пользователя в БД", userPut.getBirthday(), actualUser.getBirthday());
    }

    @Test
    public void putUserWithoutNameTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);
        assertEquals("Не произошло замены пустого имени логином пользователя", userPut.getLogin(), actualUser.getName());
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
        assertNotEquals("Не произошла замена логина", "lalala", actualUser.getLogin());
        assertNotEquals("Не произошла замена почты", "alala@test.t", actualUser.getEmail());
        assertNotEquals("Не произошла замена имени", "Liza", actualUser.getName());
        assertEquals("Не произошло замены пустого имени логином пользователя", actualUser.getLogin(), actualUser.getName());
        assertNotEquals("Не произошла замена даты рождения", LocalDate.now(), actualUser.getBirthday());

    }

    @Test
    void getUserBuIdAndWithWrongIdTest() {
        User userPut = userService.put(new User("alala@test.t", "lalala", "Liza", LocalDate.now()));
        int userId = userPut.getId();

        User actualUser = userService.get(userId);


        assertEquals("Пользователь по id не найден", userPut, actualUser);
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

        assertEquals("Пользователи не были добавлены", 3, allUsers.size());
        assertEquals("Пользователь не был добавлен в список", true, allUsers.contains(userService.get(userId1)));
        assertEquals("Пользователь не был добавлен в список", true, allUsers.contains(userService.get(userId2)));
        assertEquals("Пользователь не был добавлен в список", true, allUsers.contains(userService.get(userId3)));
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

        assertEquals("Пользователи не были добавлены", 2, usersByIds.size());
        assertEquals("Пользователь не был добавлен в список", true, usersByIds.contains(userService.get(userId1)));
        assertEquals("Пользователь не был добавлен в список", true, usersByIds.contains(userService.get(userId2)));
        assertEquals("Пользователь был добавлен в список", false, usersByIds.contains(userService.get(userId3)));
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

        assertEquals("Заявка на дружбу не была отправлена", 1, actualUser1.getFriends().size());
        assertEquals("Произошло подтверждение дружбы", 0, actualUser2.getFriends().size());

        userService.acceptFriendship(userId2, userId1);

        actualUser1 = userService.get(userId1);
        actualUser2 = userService.get(userId2);

        assertEquals("Заявка на дружбу не была отправлена", 1, actualUser1.getFriends().size());
        assertEquals("Не произошло подтверждение дружбы", 1, actualUser2.getFriends().size());

        assertThrows(UnknownDataException.class, () -> userService.acceptFriendship(userId2, userId3));
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

        assertEquals("Не произошло добавления в друзья", 1, actualUser1.getFriends().size());
        assertEquals("Не произошло подтверждение дружбы", 1, actualUser2.getFriends().size());

        userService.removeFriends(userId1, userId2);

        actualUser1 = userService.get(userId1);
        actualUser2 = userService.get(userId2);

        assertEquals("Не произошло удаления из друзей", 0, actualUser1.getFriends().size());
        assertEquals("Не произошло удаления из друзей", 0, actualUser2.getFriends().size());
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

        assertEquals("Нет общих друзей", 1, commonFriends.size());
        assertEquals("Неверный общий друг", true, commonFriends.contains(actualUser2));
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
        assertEquals("Не произошло добавления в друзья", 2, usersFriend.size());

        userService.acceptFriendship(userId2, userId1);
        userService.acceptFriendship(userId3, userId1);
        actualUser1 = userService.get(userId1);
        usersFriend = userService.getAllFriends(actualUser1.getId());
        assertEquals("Не произошло добавления в друзья", 2, usersFriend.size());
    }

    @Test
    void getStatusName(){
        String nameAccept = userService.getStatusName(0);
        String nameNotAccept = userService.getStatusName(1);
        assertEquals("Неверное имя статуса","accepted" , nameAccept);
        assertEquals("Неверное имя статуса","not accepted" , nameNotAccept);
    }

    @Test
    void putFilmTest(){
        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        Film actualFilm = filmService.get(filmId);
        assertEquals("Произошло неверное сохранение названия фильма", putFilm.getName(), actualFilm.getName());
        assertEquals("Произошло неверное сохранение описания фильма", putFilm.getDescription(), actualFilm.getDescription());
        assertEquals("Произошло неверное сохранение даты релиза фильма", putFilm.getReleaseDate(), actualFilm.getReleaseDate());
        assertEquals("Произошло неверное сохранение продолжительности фильма", putFilm.getDuration(), actualFilm.getDuration());
        assertEquals("Произошло неверное сохранение категории фильма", putFilm.getMpa(), actualFilm.getMpa());
    }

    @Test
    void getFilmWithWrongId(){
        assertThrows(UnknownDataException.class, () -> filmService.get(123));
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

        assertEquals("Произошло неверное обновление названия фильма", putFilm.getName(), actualFilm.getName());
        assertEquals("Произошло неверное обновление описания фильма", putFilm.getDescription(), actualFilm.getDescription());
        assertEquals("Произошло неверное обновление даты релиза фильма", putFilm.getReleaseDate(), actualFilm.getReleaseDate());
        assertEquals("Произошло неверное обновление продолжительности фильма", putFilm.getDuration(), actualFilm.getDuration());
        assertEquals("Произошло неверное обновление категории фильма", putFilm.getMpa(), actualFilm.getMpa());
        assertEquals("Произошло неверное обновление жанров фильма", putFilm.getGenres(), actualFilm.getGenres());
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

        assertEquals("Фильмы не были добавлены", 3, allFilm.size());
        assertEquals("Фильм не был добавлен в список", true, allFilm.contains(filmService.get(filmId1)));
        assertEquals("Фильм не был добавлен в список", true, allFilm.contains(filmService.get(filmId2)));
        assertEquals("Фильм не был добавлен в список", true, allFilm.contains(filmService.get(filmId3)));
    }

    @Test
    void addLikeTest(){
        User userPut = userService.put(new User("alala@test.t", "lalala", "alalala", LocalDate.now()));
        int userId = userPut.getId();

        Film putFilm = filmService.put(new Film("Во все тяжкие", "Сериал про двух друзей", LocalDate.of(2005, 10, 9), 100, filmService.getCategoryById(1)));
        int filmId = putFilm.getId();

        filmService.addLike(filmId, userId);
        Film actualFilm = filmService.get(filmId);
        assertEquals("Лайк не был поставлен", 1, actualFilm.getUserLikes().size());

        filmService.addLike(filmId, userId);
        Film actualFilm2 = filmService.get(filmId);
        assertEquals("Один пользователь поставил 2 лайка на один фильм", 1, actualFilm2.getUserLikes().size());

        assertThrows(UnknownDataException.class, () -> filmService.addLike(filmId, 123));
        assertThrows(UnknownDataException.class, () -> filmService.addLike(145, userId));
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
        assertEquals("Лайк не был поставлен", 1, actualFilm.getUserLikes().size());

        filmService.removeLike(filmId, userId2);
        Film actualFilm1 = filmService.get(filmId);
        assertEquals("Произошло удаление лайка от пользователя, который этот лайк не ставил", 1, actualFilm1.getUserLikes().size());

        filmService.removeLike(filmId, userId);
        Film actualFilm2 = filmService.get(filmId);
        assertEquals("Лайк не был удален", 0, actualFilm2.getUserLikes().size());

        filmService.removeLike(filmId, userId);
        Film actualFilm3 = filmService.get(filmId);
        assertNotEquals("Произошло удаление несуществующего лайка", -1, actualFilm3.getUserLikes().size());

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
        assertEquals("Фильмы не были добавлены в список", 3, top.size());
        assertEquals("Не верный порядок вывода фильмов", actualFilm3, top.get(0));
        assertEquals("Не верный порядок вывода фильмов", actualFilm1, top.get(1));
        assertEquals("Не верный порядок вывода фильмов", actualFilm2, top.get(2));
    }

    @Test
    void getAllGenresAndByIdTest(){
        List<Genre> genres= filmService.getAllGenres();
        assertEquals("Не все жанры добавлены в список", 6, genres.size());

        Genre genre1 = filmService.getGenreById(1);
        assertEquals("Поиск жанра по id не работает", genres.get(0), genre1);

        assertThrows(UnknownDataException.class, () -> filmService.getGenreById(10));
    }

    @Test
    void getAllMpaAndByIdTest(){
        List<MPA> mpas= filmService.getAllCategories();
        assertEquals("Не все категории добавлены в список", 5, mpas.size());

        MPA mpa1 = filmService.getCategoryById(1);
        assertEquals("Поиск категории по id не работает", mpas.get(0), mpa1);

        assertThrows(UnknownDataException.class, () -> filmService.getCategoryById(10));
    }


}

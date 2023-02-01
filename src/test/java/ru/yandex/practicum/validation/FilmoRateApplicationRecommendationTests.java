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
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationRecommendationTests {
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

}
package ru.yandex.practicum.storage.db;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.ReviewStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;

    private final FilmStorage filmStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public Review addReview(Review review) {
        userStorage.get(review.getUserId());//проверка на существование пользователя
        filmStorage.get(review.getFilmId());//проверка на существование фильма
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "insert into reviews(content, is_positive, user_id, film_id, USEFUL) values (?,?,?,?,0)";
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                    ps.setString(1, review.getContent());
                    ps.setBoolean(2, review.getIsPositive());
                    ps.setInt(3, review.getUserId());
                    ps.setInt(4, review.getFilmId());
                    return ps;
                }, keyHolder
        );
        review.setReviewId(keyHolder.getKey().intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        int rows = jdbcTemplate.update("update reviews set content = ?, is_positive = ?  where review_id = ?",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (rows != 0) {
            return getReviewById(review.getReviewId());
        } else {
            throw new UnknownDataException("Неизвестный отзыв");
        }
    }

    @Override
    public Review getReviewById(Integer id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select * from reviews where review_id = ?", id);
        if (sql.next()) {
            Review review = new Review(
                    sql.getString("content"),
                    sql.getBoolean("is_positive"),
                    sql.getInt("user_id"),
                    sql.getInt("film_id")
            );
            review.setIsPositive(sql.getBoolean("is_positive"));
            review.setReviewId(sql.getInt("review_id"));
            int likes = jdbcTemplate.queryForObject("select useful from REVIEWS where review_id = ?", new Object[]{id}, Integer.class);
            review.setUseful(likes);
            return review;
        } else throw new

                UnknownDataException("Не найден отзыв с данным id");

    }

    @Override
    public Review deleteReviewById(Integer id) {
        Review reviewById = getReviewById(id);
        if (Optional.of(reviewById).isPresent()) {
            jdbcTemplate.update("delete from reviews where review_id = ?", id);
            return reviewById;
        } else {
            throw new UnknownDataException("Не найден отзыв с данным id");
        }
    }

    @Override
    public Review addLikeToReview(Integer reviewId, Integer userId) {
        getReviewById(reviewId);
        userStorage.checkUser(userId);//проверяем есть ли такие айдишники, если нет - выкинет исключение
        jdbcTemplate.update("insert into review_likes(review_id, user_id, like_type) values (?,?,?)", reviewId, userId, 1);
        jdbcTemplate.update("update REVIEWS set USEFUL = USEFUL+1 where REVIEW_ID = ?", reviewId);
        getReviewById(reviewId).setUseful(getReviewById(reviewId).getReviewId() + 1);
        return getReviewById(reviewId);
    }

    @Override
    public Review addDislikeToReview(Integer reviewId, Integer userId) {
        getReviewById(reviewId);
        userStorage.checkUser(userId);//проверяем есть ли такие айдишники, если нет - выкинет исключение
        jdbcTemplate.update("insert into review_likes(review_id, user_id, like_type) values (?,?,?)", reviewId, userId, 2);
        jdbcTemplate.update("update REVIEWS set USEFUL = USEFUL - 1 where REVIEW_ID = ?", reviewId);
        getReviewById(reviewId).setUseful(getReviewById(reviewId).getReviewId() - 1);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteLikeFromReview(Integer reviewId, Integer userId) {
        getReviewById(reviewId);
        userStorage.get(userId);
        jdbcTemplate.update("delete from review_likes where review_id = ? AND user_id = ? AND like_type = 1", reviewId, userId);
        jdbcTemplate.update("update REVIEWS set USEFUL = USEFUL - 1 where REVIEW_ID = ?", reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislikeFromReview(Integer reviewId, Integer userId) {
        getReviewById(reviewId);
        userStorage.get(userId);
        jdbcTemplate.update("delete from review_likes where review_id = ? AND user_id = ? AND like_type = 2", reviewId, userId);
        jdbcTemplate.update("update REVIEWS set USEFUL = USEFUL+1 where REVIEW_ID = ?", reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        if (filmId == 0) {
            return jdbcTemplate.query("select * from reviews limit (?)",
                            new Object[]{count},
                            new BeanPropertyRowMapper<>(Review.class))
                    .stream()
                    .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                    .collect(Collectors.toList());
        } else {
            return jdbcTemplate.query("select * from reviews where film_id = ? limit (?)",
                            new Object[]{filmId, count},
                            new BeanPropertyRowMapper<>(Review.class)).stream()
                    .sorted(Comparator.comparingInt(Review::getUseful).reversed()).collect(Collectors.toList());
        }
    }
}

package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.film.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    Review getReviewById(Integer id);

    Review deleteReviewById(Integer id);

    Review addLikeToReview(Integer reviewId, Integer userId);

    Review addDislikeToReview(Integer reviewId, Integer userId);

    Review deleteLikeFromReview(Integer reviewId, Integer userId);

    Review deleteDislikeFromReview(Integer reviewId, Integer userId);

    List<Review> getAllReviews(Integer filmId, Integer count);
}

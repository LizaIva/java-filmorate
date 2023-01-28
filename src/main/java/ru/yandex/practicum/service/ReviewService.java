package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.storage.db.ReviewDbStorage;
import ru.yandex.practicum.validation.ReviewValidator;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    private final ReviewValidator reviewValidator = new ReviewValidator();

    public ReviewService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review postReview(Review review) {
        reviewValidator.validate(review);
        return reviewDbStorage.addReview(review);
    }

    public Review putReview(Review review) {
        reviewValidator.validate(review);
        return reviewDbStorage.updateReview(review);
    }

    public String deleteReview(int id) {
        return reviewDbStorage.deleteReviewById(id);
    }

    public Review getReview(int id) {
        return reviewDbStorage.getReviewById(id);
    }

    public List<Review> getReviewForFilm(Integer filmId, Integer count) {
        return reviewDbStorage.getAllReviews(filmId, count);
    }

    public Review putLike(int reviewId, int userId) {
        return reviewDbStorage.addLikeToReview(reviewId, userId);
    }

    public Review putDislike(int reviewId, int userId) {
        return reviewDbStorage.addDislikeToReview(reviewId, userId);
    }

    public Review deleteLike(int reviewId, int userId) {
        return reviewDbStorage.deleteLikeFromReview(reviewId, userId);
    }

    public Review deleteDislike(int reviewId, int userId) {
        return reviewDbStorage.deleteDislikeFromReview(reviewId, userId);
    }
}

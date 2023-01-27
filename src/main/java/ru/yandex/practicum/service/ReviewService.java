package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.event.EventType;
import ru.yandex.practicum.model.event.Operation;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.storage.db.ReviewDbStorage;
import ru.yandex.practicum.validation.ReviewValidator;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    private final ReviewValidator reviewValidator = new ReviewValidator();

    private final EventService eventService;

    public ReviewService(ReviewDbStorage reviewDbStorage, EventService eventService) {
        this.reviewDbStorage = reviewDbStorage;
        this.eventService = eventService;
    }

    public Review postReview(Review review) {
        reviewValidator.validate(review);
        Review createdReview = reviewDbStorage.addReview(review);
        eventService.putEvent(createdReview.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public Review putReview(Review review) {
        reviewValidator.validate(review);
        Review updatedReview = reviewDbStorage.updateReview(review);
        eventService.putEvent(updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }

    public String deleteReview(int id) {
        Review deletedReview = reviewDbStorage.deleteReviewById(id);
        eventService.putEvent(deletedReview.getUserId(), EventType.REVIEW, Operation.REMOVE, id);
        return "Успешное удаление";
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

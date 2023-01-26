package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review postReview(@RequestBody Review review) {
        return reviewService.postReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.putReview(review);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable int id){
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getListReview(@RequestParam(defaultValue = "0") int filmId, @RequestParam(defaultValue = "10") int count){
        return reviewService.getReviewForFilm(filmId,count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.putLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteLike(id,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable int id, @PathVariable int userId){
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteDislike(id, userId);
    }
}

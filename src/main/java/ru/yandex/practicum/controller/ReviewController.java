package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.film.Review;
import ru.yandex.practicum.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review postReview(@RequestBody Review review) {
        log.info("Получен запрос на создание отзыва");
        return reviewService.postReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва");
        return reviewService.putReview(review);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable int id) {
        log.info("Получен запрос на удаление отзыва с id = {}", id);
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        log.info("Получен запрос на вывод отзыва с id = {}", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getListReview(@RequestParam(defaultValue = "0") int filmId,
                                      @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на вывод списка из {} отзывов на фильм с id = {}", count, filmId);
        return reviewService.getReviewForFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление лайка для отзыва c id = {} от пользователя с id = {}", id, userId);
        return reviewService.putLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на добавление дизлайка для отзыва с id = {} от пользователя с id = {}", id, userId);
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка для отзыва с id = {} от пользователя с id = {}", id, userId);
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление дизлайка для отзыва с id = {} от пользователя с id = {}", id, userId);
        return reviewService.deleteDislike(id, userId);
    }
}

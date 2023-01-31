package ru.yandex.practicum.db;

import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.film.Review;

public class ReviewValidator {
    public void validate(Review review) {
        if (review.getIsPositive() == null) {
            throw new ValidationException("Тип отзыва должен быть указан");
        } else if (review.getContent() == null) {
            throw new ValidationException("Описание не может быть не задано");
        } else if (review.getUserId() == null) {
            throw new ValidationException("Пользователь должен быть задан");
        } else if (review.getFilmId() == null) {
            throw new ValidationException("Фильм должен быть задан");
        }
    }
}

package ru.yandex.practicum.model.film;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Review {
    int reviewId;

    @NonNull
    String content;

    @NonNull
    Boolean isPositive;

    @NonNull
    Integer userId;

    @NonNull
    Integer filmId;

    Integer useful = 0;
}

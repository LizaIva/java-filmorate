package ru.yandex.practicum.model.film;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Review {
    private int reviewId;

    @NonNull
    private String content;

    @NonNull
    private Boolean isPositive;

    @NonNull
    private Integer userId;

    @NonNull
    private Integer filmId;

    private Integer useful = 0;
}

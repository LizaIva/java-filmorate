package ru.yandex.practicum.model.film;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
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

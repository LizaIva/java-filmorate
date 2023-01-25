package ru.yandex.practicum.model.film;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Director {
    @Positive
    private Integer id;
    @NotBlank(message = "Имя режессера не может быть пустым")
    private String name;
}

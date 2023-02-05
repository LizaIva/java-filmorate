package ru.yandex.practicum.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MPA {
    @NotNull
    private Integer id;

    private String name;

    public MPA(Integer id) {
        this.id = id;
    }
}

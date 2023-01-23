package ru.yandex.practicum.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = "id")
public class MPA {

    @NotNull
    private Integer id;

    private String name;

    public MPA() {
    }

    public MPA(Integer id) {
        this.id = id;
    }

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}

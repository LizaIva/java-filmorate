package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int counter = 0;

    @Override
    public Film put(Film film) {
        if (film.getId() == null) {
            film.setId(++counter);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Integer id) {
        Film film = films.get(id);

        if (film == null) {
            throw new UnknownDataException("Фильм с данным id не существует");
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> values = new ArrayList<>(films.values());
        values.sort(Comparator.comparing(film -> film.getUserLikes().size(), Comparator.reverseOrder()));

        return values;
    }
}

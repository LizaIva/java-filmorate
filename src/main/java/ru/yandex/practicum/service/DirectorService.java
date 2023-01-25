package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director getDirector(int id) {
        return directorStorage.getDirector(id);
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirector();
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public int deleteDirector(int id) {
        return directorStorage.deleteDirector(id);
    }
}

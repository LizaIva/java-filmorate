package ru.yandex.practicum.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        String sqlQuery = "insert into director (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().intValue());

        return director;
    }

    @Override
    public Director getDirector(int id) {
        String sqlQuery = "select * from director where director_id = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!directorRows.next()) {
            throw new UnknownDataException("Director с id = " + id + " не найден");
        }

        return jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id);
    }

    @Override
    public List<Director> getAllDirector() {
        String sqlQuery = "select * from director";

        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "update director set name = ? where director_id = ?";

        getDirector(director.getId());
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return director;
    }

    @Override
    public int deleteDirector(int id) {
        String sqlQuery = "delete from director where director_id = ?";

        getDirector(id);
        jdbcTemplate.update(sqlQuery, id);

        return id;
    }


    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");

        return new Director(id, name);
    }

    @Override
    public List<Director> getDirectorsByFilm(int id) {
        String sqlQuery = "select * from director where director_id in (" +
                "select director_id from film_director where film_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::makeDirector, id);
    }

    @Override
    public void addFilmDirector(int filmId, List<Director> list) {
        String sqlQuery = "insert into film_director (film_id, director_id) values (?, ?)";

        if (list == null || list.isEmpty()) {
            return;
        }

        for (Director director : list) {
            jdbcTemplate.update(sqlQuery, filmId, director.getId());
        }
    }
}

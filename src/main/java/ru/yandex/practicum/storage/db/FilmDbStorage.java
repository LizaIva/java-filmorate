package ru.yandex.practicum.storage.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.storage.FilmStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
public class FilmDbStorage implements FilmStorage {

    private static final String UPDATE_FILM_TITLE_QUERY = "update film set TITLE = %s where FILM_ID = %d";

    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film put(Film film) {
        String sqlQuery = "insert into film (title, description, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, new java.sql.Date(Date.valueOf(film.getReleaseDate()).getTime()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        putGenre(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public void updateGenre(int filmId, List<Genre> genreIds) {
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRE WHERE FILM_ID= ? ",
                filmId
        );

        putGenre(filmId, genreIds);
    }

    private void putGenre(int filmId, List<Genre> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        String value = "(%d,%d)";
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO FILM_GENRE VALUES ");

        for (int i = 0; i < genreIds.size(); i++) {
            queryBuilder.append(String.format(value, filmId, genreIds.get(i).getId()));

            if (genreIds.size() != i + 1) {
                queryBuilder.append(",");
            }
        }

        jdbcTemplate.update(queryBuilder.toString());
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmForUpdate = get(film.getId());

        StringBuilder query = new StringBuilder("update film set ");
        List<Object> args = new LinkedList<>();

        if (film.getName() != null) {
            query.append("TITLE = ?");
            args.add(film.getName());

            filmForUpdate.setName(film.getName());
        }
        if (film.getDescription() != null) {
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("DESCRIPTION = ?");
            args.add(film.getDescription());

            filmForUpdate.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null) {
            if(!args.isEmpty()) {
                query.append(", ");
            }
            query.append("RELEASE_DATE = ?");
            args.add(film.getReleaseDate());

            filmForUpdate.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null) {
            if(!args.isEmpty()) {
                query.append(", ");
            }
            query.append("DURATION = ?");
            args.add(film.getDuration());
            filmForUpdate.setDuration(film.getDuration());
        }

        if (film.getMpa() != null) {
            if(!args.isEmpty()) {
                query.append(", ");
            }

            query.append("MPA_ID = ?");
            args.add(film.getMpa().getId());

            filmForUpdate.setMpa(film.getMpa());
        }

        if (!args.isEmpty()) {
            query.append(" where FILM_ID = ?");
            args.add(film.getId());

            jdbcTemplate.update(query.toString(), args.toArray(Object[]::new));
        }

        return filmForUpdate;
    }


    @Override
    public Film get(Integer id) {
        return jdbcTemplate.query(
                "select f.*, M.NAME as mpa_name from film f join MPA M on M.MPA_ID = f.MPA_ID where film_id = ?",
                rs -> {
                    if (!rs.first() && !rs.next()) {
                        throw new UnknownDataException("Фильма с данным id не существует");
                    }
                    return mapFilmData(rs);
                },
                id
        );
    }

    @Override
    public Film deleteById(int id) {
        Film film = get(id);
        final String sqlDeleteQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        final String sqlDeleteGenreQuery = "DELETE FROM film_genre WHERE film_id = ?";
        final String sqlDeleteLikesQuery = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteQuery, id);
        jdbcTemplate.update(sqlDeleteGenreQuery, id);
        jdbcTemplate.update(sqlDeleteLikesQuery, id);
        log.info("Фильм {} с id = {} удален", film.getName(), film.getId());
        return film;
    }

    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query("select *, M.NAME as mpa_name from film join MPA M on M.MPA_ID = FILM.MPA_ID",
                (rs, rowNum) -> mapFilmData(rs)
        );
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(
                "INSERT INTO FILM_LIKES VALUES ( ?,? )",
                filmId,
                userId
        );
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update(
                "DELETE FROM FILM_LIKES WHERE FILM_ID= ? AND USER_ID = ?",
                filmId,
                userId
        );
    }

    @Override
    public List<Film> getTop(Integer limit) {
        String sqlQuery = "SELECT *, M.NAME as mpa_name " +
                "FROM film AS f LEFT JOIN (SELECT film_id, " +
                "                       COUNT(user_id) as likes_count " +
                "                       FROM FILM_LIKES " +
                "                       GROUP BY film_id) " +
                "AS film_likes_count ON f.film_id = film_likes_count.film_id join MPA M on M.MPA_ID = f.MPA_ID " +
                "ORDER BY film_likes_count.likes_count DESC NULLS LAST " +
                "LIMIT ?;";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> mapFilmData(rs),
                limit
        );
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRE;";
        return jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")));
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID=?;";
        return jdbcTemplate.query(sqlQuery,
                rs -> {
                    if (!rs.first()) {
                        throw new UnknownDataException("Фильма с данным id не существует");
                    }
                    return new Genre(rs.getInt("genre_id"), rs.getString("name"));
                },
                genreId);
    }

    @Override
    public List<MPA> getAllCategories() {
        String sqlQuery = "SELECT * FROM MPA;";
        return jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> new MPA(rs.getInt("mpa_id"), rs.getString("name")));
    }

    @Override
    public MPA getCategoryById(int categoryId) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID=?;";
        return jdbcTemplate.query(sqlQuery,
                rs -> {
                    if (!rs.first()) {
                        throw new UnknownDataException("Фильма с данным id не существует");
                    }
                    return new MPA(rs.getInt("mpa_id"), rs.getString("name"));
                },
                categoryId);
    }

    private Film mapFilmData(ResultSet rs) throws SQLException {
        Film film = new Film(
                rs.getString("title"),
                rs.getString("description"),
                rs.getObject("release_date", LocalDate.class),
                rs.getInt("duration"),
                new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name"))
        );
        film.setId(rs.getInt("film_id"));

        String sqlQuerySelectLikes = "SELECT user_id FROM FILM_LIKES WHERE FILM_ID = ?";

        List<Integer> userLikes = jdbcTemplate.queryForList(
                sqlQuerySelectLikes, Integer.class, film.getId()
        );

        film.setUserLikes(new HashSet<>(userLikes));

        String sqlQuerySelectGenre = "SELECT G2.GENRE_ID, NAME FROM FILM_GENRE join GENRE G2 on G2.GENRE_ID = FILM_GENRE.GENRE_ID WHERE FILM_ID = ?";

        List<Genre> filmGenres = jdbcTemplate.query(
                sqlQuerySelectGenre,
                (grs, rowNum) -> new Genre(grs.getInt("genre_id"), grs.getString("name")),
                film.getId()
        );

        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public void checkFilm(int id) {
        String checkQuery = "SELECT * FROM FILM WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkQuery, id);
        if (!filmRows.next()) {
            log.warn("Фильм с идентификатором {} не найден.", id);
            throw new UnknownDataException("Фильм c id = " + id + " не найден");
        }
    }
}
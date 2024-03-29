package ru.yandex.practicum.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.UnknownDataException;
import ru.yandex.practicum.model.film.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.film.Genre;
import ru.yandex.practicum.model.film.MPA;
import ru.yandex.practicum.storage.DirectorStorage;
import ru.yandex.practicum.storage.FilmStorage;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorStorage directorStorage;

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

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        film.setId(keyHolder.getKey().intValue());
        putGenre(film.getId(), film.getGenres());
        film.setDirectors(putDirector(film.getId(), film.getDirectors()));
        directorStorage.addFilmDirector(film.getId(), film.getDirectors());

        return film;
    }

    @Override
    public void updateGenre(int filmId, List<Genre> genreIds) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ? ", filmId);
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

    private List<Director> putDirector(int filmId, List<Director> directorIds) {
        jdbcTemplate.update("DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?", filmId);

        if (directorIds == null || directorIds.isEmpty()) {
            return new ArrayList<>();
        }

        String value = "(%d,%d)";
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO FILM_DIRECTOR VALUES ");
        List<Director> listDirectors = new ArrayList<>();

        for (int i = 0; i < directorIds.size(); i++) {
            listDirectors.add(directorStorage.getDirector(directorIds.get(i).getId()));
            queryBuilder.append(String.format(value, filmId, directorIds.get(i).getId()));

            if (directorIds.size() != i + 1) {
                queryBuilder.append(",");
            }
        }

        jdbcTemplate.update(queryBuilder.toString());

        return listDirectors;
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
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("RELEASE_DATE = ?");
            args.add(film.getReleaseDate());

            filmForUpdate.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null) {
            if (!args.isEmpty()) {
                query.append(", ");
            }
            query.append("DURATION = ?");
            args.add(film.getDuration());
            filmForUpdate.setDuration(film.getDuration());
        }

        if (film.getMpa() != null) {
            if (!args.isEmpty()) {
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


        filmForUpdate.setDirectors(putDirector(film.getId(), film.getDirectors()));
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
        String sqlDeleteQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        Film film = get(id);

        jdbcTemplate.update(sqlDeleteQuery, id);
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
    public void addLike(int filmId, int userId, Integer userMark) {
        jdbcTemplate.update("INSERT INTO FILM_LIKES VALUES (?, ?, ?)", filmId, userId, userMark);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
    }

    @Override
    public List<Film> getTop(Integer limit) {
        //to do
        //Максим
        //поправить вывод фильмов не по лайкам а по оценкам
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

    @Override
    public List<Film> getFilmsDirectorSortedByYear(int directorId) {
        String sqlQuery = "SELECT *, M.NAME AS mpa_name FROM film JOIN MPA M ON M.MPA_ID = FILM.MPA_ID" +
                " WHERE film_id IN (" +
                "SELECT film_id FROM film_director WHERE director_id = ?) " +
                "ORDER BY release_date";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilmData(rs), directorId);
    }

    @Override
    public List<Film> getFilmsDirectorSortedByLikes(int directorId) {
        //TODO максим
        String sqlQuery = "SELECT *, M.NAME AS mpa_name from film join MPA M on M.MPA_ID = FILM.MPA_ID " +
                "LEFT JOIN film_likes fl ON film.film_id = fl.film_id " +
                "WHERE film.film_id IN (" +
                "SELECT film_id FROM film_director WHERE director_id = ?) " +
                "GROUP BY film.film_id " +
                "ORDER BY AVG(fl.mark) DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilmData(rs), directorId);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        //TODO максим
        String sqlQuery = "SELECT f.FILM_ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME AS mpa_name " +
                "FROM FILM AS f LEFT JOIN MPA AS m on m.MPA_ID = f.MPA_ID WHERE FILM_ID IN " +
                "(SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID IN " +
                "(SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?));";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFilmData(rs), userId, friendId).stream()
                .sorted((f1, f2) -> f2.getMiddleRating() - f1.getMiddleRating())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findLimitPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        //TODO максим
        String sqlQuery = "SELECT f.*, m.mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(FL.USER_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> mapFilmData(rs),
                genreId, year, count
        ).stream().sorted((f1, f2) -> f2.getMiddleRating() - f1.getMiddleRating()).collect(Collectors.toList());
    }

    @Override
    public List<Film> findPopularFilmsByYearAndGenre(Integer year, Integer genreId) {
        //TODO максим
        String sqlQuery = "SELECT f.*, m.mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(FL.USER_ID) DESC";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> mapFilmData(rs),
                genreId, year
        ).stream().sorted((f1, f2) -> f2.getMiddleRating() - f1.getMiddleRating()).collect(Collectors.toList());
    }

    @Override
    public List<Film> findPopularFilmsByYear(Integer year) {
        ///TODO максим
        String sqlQuery = "SELECT f.*, m.mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(FL.USER_ID) DESC";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> mapFilmData(rs),
                year
        ).stream().sorted((f1, f2) -> f2.getMiddleRating() - f1.getMiddleRating()).collect(Collectors.toList());
    }

    @Override
    public List<Film> findPopularFilmsByGenre(Integer genreId) {
        //TODO максим
        String sqlQuery = "SELECT f.*, m.mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY COUNT(FL.USER_ID) DESC";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> mapFilmData(rs),
                genreId
        ).stream().sorted((f1, f2) -> f2.getMiddleRating() - f1.getMiddleRating()).collect(Collectors.toList());
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
        film.setDirectors(putDirector(film.getId(), directorStorage.getDirectorsByFilm(film.getId())));

        String sqlGetMiddleRating = "SELECT AVG(mark) FROM film_likes where film_id = ?";
        film.setMiddleRating(jdbcTemplate.queryForObject(sqlGetMiddleRating, new Object[]{film.getId()}, Integer.class));
        if (film.getMiddleRating() == null) {
            film.setMiddleRating(0);
        }
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
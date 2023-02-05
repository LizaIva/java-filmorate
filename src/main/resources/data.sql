DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
DELETE FROM film;
ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1;
DELETE FROM mpa;
ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1;
DELETE FROM genre;
ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1;
DELETE FROM director;
ALTER TABLE director ALTER COLUMN director_id RESTART WITH 1;
DELETE FROM event_feed;
ALTER TABLE event_feed ALTER COLUMN event_id RESTART WITH 1;
DELETE FROM reviews;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;




MERGE INTO genre (genre_id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO mpa (mpa_id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO status (status_id, name)
    VALUES (0, 'accepted'),
           (1, 'not accepted');

MERGE INTO like_type (like_id, name)
    VALUES (1, 'like'),
           (2, 'dislike');
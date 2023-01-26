MERGE INTO genre (genre_id, name)
VALUES (1, 'Комедия') ,
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

MERGE INTO like_type(like_id, name)
VALUES (1, 'like'),
       (2, 'dislike');
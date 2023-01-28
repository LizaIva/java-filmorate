CREATE TABLE IF NOT EXISTS genre
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name   VARCHAR NOT NULL
);


CREATE TABLE IF NOT EXISTS status
(
    status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name      VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title        VARCHAR      NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE,
    duration     INTEGER      NOT NULL,
    mpa_id       INTEGER,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR NOT NULL,
    login    VARCHAR NOT NULL UNIQUE,
    name     VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS user_friends
(
    user_id   INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id INTEGER,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    status_id INTEGER DEFAULT 1,
    FOREIGN KEY (status_id) REFERENCES status (status_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id INTEGER,
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     varchar,
    is_positive boolean,
    user_id     INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    film_id     integer,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    useful      integer
);
CREATE TABLE IF NOT EXISTS like_type
(
    like_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    varchar
);
CREATE TABLE IF NOT EXISTS review_likes
(
    review_id integer,
    FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id   INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    like_type integer,
    FOREIGN KEY (like_type) REFERENCES like_type (like_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS director
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id     INTEGER,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    director_id INTEGER,
    FOREIGN KEY (director_id) REFERENCES director (director_id) ON DELETE CASCADE
);
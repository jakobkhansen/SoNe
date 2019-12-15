BEGIN TRANSACTION;

DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE TABLE users (
    userId SERIAL PRIMARY KEY,
    username text,
    hashed_password varchar(128),
    salt varchar(22)
);

CREATE TABLE posts (
    postId SERIAL PRIMARY KEY,
    content varchar(280),
    likes int,
    posted_at DATE
);

COMMIT;

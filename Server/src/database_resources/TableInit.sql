BEGIN TRANSACTION;

DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE TABLE users (
    userId SERIAL PRIMARY KEY,
    username text UNIQUE,
    hashed_password varchar(128),
    salt varchar(22)

    CONSTRAINT USERNAME_LENGTH CHECK (length(username) > 0)
);

CREATE TABLE posts (
    postId SERIAL PRIMARY KEY,
    postedByUser int REFERENCES users(userId),
    content varchar(280),
    likes int DEFAULT 0,
    posted_at TIMESTAMP DEFAULT now()::timestamp(0)
);

CREATE TABLE following (
    user1Id int REFERENCES users(userId),
    user2Id int REFERENCES users(userId)
);

COMMIT;

CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    login    TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

CREATE TABLE tokens
(
    value   TEXT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users
);

CREATE TABLE tasks
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users,
    phrase TEXT NOT NULL,
    status TEXT NOT NULL
);

CREATE TABLE results
(
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks,
    file TEXT NOT NULL,
    number_line INT NOT NULL,
    line TEXT NOT NULL
)

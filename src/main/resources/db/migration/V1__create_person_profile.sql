CREATE TABLE IF NOT EXISTS profile
(
    id          UUID PRIMARY KEY,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,
    birth_date  DATE NOT NULL
)

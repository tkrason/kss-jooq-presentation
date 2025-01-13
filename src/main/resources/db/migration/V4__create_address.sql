CREATE TABLE IF NOT EXISTS address
(
    id            UUID PRIMARY KEY,
    street        TEXT NOT NULL,
    street_number INT NOT NULL,
    city          TEXT NOT NULL
);

ALTER TABLE profile ADD COLUMN address_id UUID NOT NULL REFERENCES address;

CREATE TABLE IF NOT EXISTS interest
(
    id   UUID PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS profile_interest
(
    id          UUID PRIMARY KEY,
    profile_id  UUID NOT NULL REFERENCES profile,
    interest_id UUID NOT NULL REFERENCES interest
);

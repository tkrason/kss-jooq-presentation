CREATE TABLE IF NOT EXISTS profile_picture
(
    id          UUID PRIMARY KEY,
    profile_id  UUID        NOT NULL REFERENCES profile,
    file_url    TEXT        NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL
)

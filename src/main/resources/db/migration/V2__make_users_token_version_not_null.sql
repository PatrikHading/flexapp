UPDATE users
SET token_version = 0
WHERE token_version IS NULL;

ALTER TABLE users
    ALTER COLUMN token_version SET DEFAULT 0;

ALTER TABLE users
    ALTER COLUMN token_version SET NOT NULL;
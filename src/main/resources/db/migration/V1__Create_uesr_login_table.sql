CREATE TABLE "users" (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    encoded_password VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE "logins" (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    auth_result VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
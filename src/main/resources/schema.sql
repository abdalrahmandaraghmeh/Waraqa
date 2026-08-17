CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS books (
                                     book_id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    images_url TEXT[] NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_book_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS majors CASCADE;
DROP TABLE IF EXISTS faculties CASCADE;
DROP TABLE IF EXISTS universities CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
     user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    rating DOUBLE PRECISION DEFAULT 0.0,
    total_sales INT DEFAULT 0,
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS universities (
    id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS faculties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    university_id BIGINT NOT NULL REFERENCES universities(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS majors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    faculty_id BIGINT NOT NULL REFERENCES faculties(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS books (
     id BIGSERIAL PRIMARY KEY,
     title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    description TEXT DEFAULT '',
    price DECIMAL(10, 2) NOT NULL,
    listing_type VARCHAR(50) DEFAULT 'for_sale',
    exchange_for TEXT,
    condition VARCHAR(50) DEFAULT 'good',
    category VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    sub_type VARCHAR(50),
    edition VARCHAR(100),
    cover_image VARCHAR(500),
    images_url TEXT[],
    views_count INT DEFAULT 0,
    saves_count INT DEFAULT 0,
    publisher_id BIGINT NOT NULL,
    university_id BIGINT,
    faculty_id BIGINT,
    major_id BIGINT,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_books_university FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_major FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE SET NULL
    );

-- Seed initial data
INSERT INTO universities (name) VALUES ('Jordan University of Science and Technology');
INSERT INTO faculties (name, university_id) VALUES ('Faculty of Computer and Information Technology', 1);
INSERT INTO majors (name, faculty_id) VALUES ('Software Engineering', 1);
INSERT INTO majors (name, faculty_id) VALUES ('Computer Science', 1);
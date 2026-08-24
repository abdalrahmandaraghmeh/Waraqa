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
    password VARCHAR(255) NOT NULL
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
    author VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    published_at TIMESTAMP NOT NULL,
    cover_image VARCHAR(500),
    category VARCHAR(50) NOT NULL, -- academic / general
    type VARCHAR(50), -- book / novel
    rating DOUBLE PRECISION DEFAULT 0.0,
    publisher_id BIGINT NOT NULL,
    university_id BIGINT,
    faculty_id BIGINT,
    major_id BIGINT,
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_books_university FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_major FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE SET NULL
);

-- Seed some initial data for testing
INSERT INTO universities (name) VALUES ('Jordan University of Science and Technology');
INSERT INTO faculties (name, university_id) VALUES ('Faculty of Computer and Information Technology', 1);
INSERT INTO majors (name, faculty_id) VALUES ('Software Engineering', 1);
INSERT INTO majors (name, faculty_id) VALUES ('Computer Science', 1);
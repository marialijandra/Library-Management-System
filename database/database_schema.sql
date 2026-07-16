-- Combined & Fixed Database Schema for Library Management System
-- Compatible with MySQL and MariaDB (XAMPP / phpMyAdmin)
--
-- Target Database: iacademy_library
-- Used by both dev and registerLogin projects

CREATE DATABASE IF NOT EXISTS iacademy_library;
USE iacademy_library;

-- Drop tables in reverse order of dependency to avoid foreign key constraints issues
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    first_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(191) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'librarian', 'student') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. Books Table
CREATE TABLE books (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    quantity INT DEFAULT 0,
    image_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. Transactions Table
CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    book_id VARCHAR(36) NOT NULL,
    status ENUM('borrowed', 'returned') DEFAULT 'borrowed',
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Seed Users (Admin, Librarian, Student accounts from registerLogin test schema)
INSERT INTO users (id, first_name, surname, email, password, role) VALUES
('07daac18-7f9d-11f1-b6a7-e89c25479b6b', 'Joseph', 'Dela Cruz', 'Joseph@library.com', '12345', 'librarian'),
('3529a6cc-7eca-11f1-b36e-e89c25479b6b', 'System', 'Admin', 'admin@library.com', 'root', 'admin'),
('428990ab-7f9f-11f1-b6a7-e89c25479b6b', 'John', 'Doe', 'John@iacademy.edu.ph', '123', 'student');

-- Seed Sample Books
INSERT INTO books (id, title, description, quantity, image_url) VALUES
('e7f1b2c3-4d5e-6f7a-8b9c-0d1e2f3a4b5c', 'To Kill a Mockingbird', 'A moral coming-of-age story set in the Depression-era South.', 4, 'https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1612238791i/56916837.jpg'),
('f8a2b3c4-5d6e-7f8a-9b0c-1d2e3f4a5b6c', 'Clean Code', 'A handbook of practices for writing readable, maintainable software.', 3, NULL),
('a9b3c4d5-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '1984', 'A dystopian vision of a totalitarian surveillance state.', 6, 'https://www.penguin.co.uk/_next/image?url=https%3A%2F%2Fcdn.penguin.co.uk%2Fdam-assets%2Fbooks%2F9780141036144%2F9780141036144-jacket-large.jpg&w=614&q=100');
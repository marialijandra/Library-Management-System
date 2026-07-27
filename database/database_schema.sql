-- Combined & Fixed Database Schema for Library Management System
-- Compatible with MySQL and MariaDB (XAMPP / phpMyAdmin)

CREATE DATABASE IF NOT EXISTS iacademy_library;
USE iacademy_library;

-- Drop tables in reverse order of dependency
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users Table
CREATE TABLE users (
   id VARCHAR(36) PRIMARY KEY,
   first_name VARCHAR(100) NOT NULL,
   surname VARCHAR(100) NOT NULL,
   email VARCHAR(191) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   role ENUM('admin', 'librarian', 'student') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. Books Table
CREATE TABLE books (
   id INT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(255) NOT NULL UNIQUE,
   description TEXT NOT NULL,
   quantity INT NOT NULL CHECK (quantity >= 0),
   image_url VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. Transactions Table
CREATE TABLE transactions (
   id VARCHAR(36) PRIMARY KEY,
   user_id VARCHAR(36) NOT NULL,
   book_id INT NOT NULL,
   status ENUM('borrowed', 'returned') DEFAULT 'borrowed',
   CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
   CONSTRAINT fk_transactions_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Seed Users
INSERT INTO users (id, first_name, surname, email, password, role) VALUES
   ('07daac18-7f9d-11f1-b6a7-e89c25479b6b', 'Joseph', 'Dela Cruz', 'Joseph@library.com', '12345', 'librarian'),
   ('3529a6cc-7eca-11f1-b36e-e89c25479b6b', 'System', 'Admin', 'admin@library.com', 'root', 'admin'),
   ('428990ab-7f9f-11f1-b6a7-e89c25479b6b', 'John', 'Doe', 'John@iacademy.edu.ph', '123', 'student');

-- Seed Sample Books
INSERT INTO books (title, description, quantity, image_url) VALUES
    ('To Kill a Mockingbird', 'A moral coming-of-age story set in the Depression-era South.', 4, 'https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1612238791i/56916837.jpg'),
    ('Clean Code', 'A handbook of practices for writing readable, maintainable software.', 3, NULL),
    ('1984', 'A dystopian vision of a totalitarian surveillance state.', 6, 'https://www.penguin.co.uk/_next/image?url=https%3A%2F%2Fcdn.penguin.co.uk%2Fdam-assets%2Fbooks%2F9780141036144%2F9780141036144-jacket-large.jpg&w=614&q=100');


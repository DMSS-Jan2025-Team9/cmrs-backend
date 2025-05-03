-- V1__create_schema.sql

CREATE DATABASE IF NOT EXISTS recommendation_db;
USE recommendation_db;

CREATE TABLE IF NOT EXISTS recommendation (
    recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
    program_id INT NOT NULL,
    course_id INT NOT NULL
);

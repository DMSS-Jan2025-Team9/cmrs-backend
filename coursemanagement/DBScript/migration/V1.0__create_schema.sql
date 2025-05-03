-- V1__create_schema.sql

-- Create Database and Use Course Database (if needed)
CREATE DATABASE IF NOT EXISTS course_db;
USE course_db;

-- Create course table
CREATE TABLE IF NOT EXISTS course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    course_code VARCHAR(50) UNIQUE NOT NULL,
    registration_start DATETIME NOT NULL,
    registration_end DATETIME NOT NULL,
    max_capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    course_desc VARCHAR(1000)
);

-- Create program table
CREATE TABLE IF NOT EXISTS program (
    program_id INT AUTO_INCREMENT PRIMARY KEY,
    program_name VARCHAR(255) NOT NULL,
    program_desc VARCHAR(1000)
);

-- Create program_course relationship table
CREATE TABLE IF NOT EXISTS program_course (
    program_id INT,
    course_id INT,
    PRIMARY KEY (program_id, course_id),
    FOREIGN KEY (program_id) REFERENCES program(program_id),
    FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- Create class table
CREATE TABLE IF NOT EXISTS class (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_capacity INT NOT NULL,
    vacancy INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id)
);

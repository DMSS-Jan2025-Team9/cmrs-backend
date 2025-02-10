CREATE DATABASE recommendation_db;
USE recommendation_db;

CREATE TABLE recommendation (
    recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
    program_id INT NOT NULL,
    course_id INT NOT NULL
);

/* Sample data */
INSERT INTO recommendation (program_id, course_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(1, 2),
(2, 2);